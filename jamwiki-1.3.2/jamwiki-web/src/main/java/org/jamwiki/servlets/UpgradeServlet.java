/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, version 2.1, dated February 1999.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the latest version of the GNU Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (LICENSE.txt); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jamwiki.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.Environment;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiConfiguration;
import org.jamwiki.WikiException;
import org.jamwiki.WikiMessage;
import org.jamwiki.WikiVersion;
import org.jamwiki.db.DatabaseUpgrades;
import org.jamwiki.db.WikiDatabase;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicVersion;
import org.jamwiki.model.VirtualWiki;
import org.jamwiki.model.WikiUser;
import org.jamwiki.parser.LinkUtil;
import org.jamwiki.parser.WikiLink;
import org.jamwiki.utils.WikiCache;
import org.jamwiki.utils.WikiLogger;
import org.jamwiki.utils.WikiUtil;
import org.springframework.web.servlet.ModelAndView;

/**
 * Used to automatically handle JAMWiki upgrades, including configuration and
 * data modifications.
 *
 * @see org.jamwiki.servlets.SetupServlet
 */
public class UpgradeServlet extends JAMWikiServlet {

	private static final WikiLogger logger = WikiLogger.getLogger(UpgradeServlet.class.getName());
	/** The name of the JSP file used to render the servlet output. */
	protected static final String JSP_UPGRADE = "upgrade.jsp";
	private static final int MAX_TOPICS_FOR_AUTOMATIC_UPDATE = 1000;

	/**
	 * This servlet requires slightly different initialization parameters from most
	 * servlets.
	 */
	public UpgradeServlet() {
		this.layout = false;
		this.displayJSP = "upgrade";
	}

	/**
	 * This method handles the request after its parent class receives control.
	 *
	 * @param request - Standard HttpServletRequest object.
	 * @param response - Standard HttpServletResponse object.
	 * @return A <code>ModelAndView</code> object to be handled by the rest of the Spring framework.
	 */
	public ModelAndView handleJAMWikiRequest(HttpServletRequest request, HttpServletResponse response, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		if (!WikiUtil.isUpgrade()) {
			throw new WikiException(new WikiMessage("upgrade.error.notrequired"));
		}
		String function = request.getParameter("function");
		pageInfo.setPageTitle(new WikiMessage("upgrade.title", Environment.getValue(Environment.PROP_BASE_WIKI_VERSION), WikiVersion.CURRENT_WIKI_VERSION));
		if (!StringUtils.isBlank(function) && function.equals("upgrade")) {
			upgrade(request, next, pageInfo);
		} else {
			view(request, next, pageInfo);
		}
		return next;
	}

	/**
	 *
	 */
	private void upgrade(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) {
		try {
			WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
			if (oldVersion.before(1, 0, 0)) {
				throw new WikiException(new WikiMessage("upgrade.error.oldversion", WikiVersion.CURRENT_WIKI_VERSION, "1.0.0"));
			}
			// first perform database upgrades
			this.upgradeDatabase(true, pageInfo.getMessages());
			if (oldVersion.before(1, 2, 0)) {
				// old installs used the DOCROOT storage type rather than the new DEFAULT
				Environment.setValue(Environment.PROP_FILE_UPLOAD_STORAGE, WikiBase.UPLOAD_STORAGE.DOCROOT.toString());
				Environment.saveConfiguration();
				// move system topics
				this.updateSystemTopics(request, pageInfo.getMessages());
			}
			// upgrade the search index if required & possible
			this.upgradeSearchIndex(true, pageInfo.getMessages());
			// upgrade stylesheet
			if (this.upgradeStyleSheetRequired()) {
				this.upgradeStyleSheet(request, pageInfo.getMessages());
			}
			pageInfo.getMessages().addAll(ServletUtil.validateSystemSettings(Environment.getInstance()));
			try {
				Environment.setValue(Environment.PROP_BASE_WIKI_VERSION, WikiVersion.CURRENT_WIKI_VERSION);
				Environment.saveConfiguration();
				// reset data handler and other instances.  this probably hides a bug
				// elsewhere since no reset should be needed, but it's anyone's guess
				// where that might be...
				WikiBase.reload();
			} catch (Exception e) {
				logger.error("Failure during upgrade while saving properties and executing WikiBase.reload()", e);
				throw new WikiException(new WikiMessage("upgrade.error.nonfatal", e.toString()));
			}
		} catch (WikiException e) {
			pageInfo.addError(e.getWikiMessage());
		}
		if (!pageInfo.getErrors().isEmpty()) {
			pageInfo.addError(new WikiMessage("upgrade.caption.upgradefailed"));
			next.addObject("failure", "true");
		} else {
			handleUpgradeSuccess(request, next, pageInfo);
		}
		this.view(request, next, pageInfo);
	}
		
	/**
	 *
	 */
	private void handleUpgradeSuccess(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) {
		WikiMessage wm = new WikiMessage("upgrade.caption.upgradecomplete");
		VirtualWiki virtualWiki = VirtualWiki.defaultVirtualWiki();
		WikiLink wikiLink = new WikiLink(request.getContextPath(), virtualWiki.getName(), virtualWiki.getRootTopicName());
		try {
			String htmlLink = LinkUtil.buildInternalLinkHtml(wikiLink, virtualWiki.getRootTopicName(), null, null, true);
			// do not escape the HTML link
			wm.setParamsWithoutEscaping(new String[]{htmlLink});
		} catch (DataAccessException e) {
			// building a link to the start page shouldn't fail, but if it does display a message
			wm = new WikiMessage("upgrade.error.nonfatal", e.toString());
			logger.warn("Upgrade complete, but unable to build redirect link to the start page.", e);
		}
		next.addObject("successMessage", wm);
		// force logout to ensure current user will be re-validated.  this is
		// necessary because the upgrade may have changed underlying data structures.
		SecurityContextHolder.clearContext();
	}

	/**
	 *
	 */
	private void renameSystemTopic(HttpServletRequest request, List<WikiMessage> messages, String virtualWiki, WikiUser wikiUser, String fromTopicName, String toTopicName) throws DataAccessException, WikiException {
		String ipAddress = ServletUtil.getIpAddress(request);
		// TODO - delete this method once the ability to upgrade to 1.2.0 has been removed.
		Topic fromTopic = WikiBase.getDataHandler().lookupTopic(virtualWiki, fromTopicName, false);
		WikiBase.getDataHandler().moveTopic(fromTopic, toTopicName, wikiUser, ipAddress, "Automatically moved by system upgrade");
		// the old topic should no longer be admin-only
		Topic topic = WikiBase.getDataHandler().lookupTopic(virtualWiki, fromTopicName, false);
		topic.setAdminOnly(false);
		TopicVersion topicVersion = new TopicVersion(wikiUser, ipAddress, null, topic.getTopicContent(), 0);
		topicVersion.setEditType(TopicVersion.EDIT_PERMISSION);
		WikiBase.getDataHandler().writeTopic(topic, topicVersion, null, null);
		String[] params = new String[3];
		params[0] = fromTopicName;
		params[1] = toTopicName;
		params[2] = virtualWiki;
		messages.add(new WikiMessage("upgrade.message.120.topic.rename", params));
	}

	/**
	 *
	 */
	private boolean updateSystemTopics(HttpServletRequest request, List<WikiMessage> messages) {
		// TODO - delete this method once the ability to upgrade to 1.2.0 has been removed.
		WikiUser wikiUser = ServletUtil.currentWikiUser();
		try {
			List<VirtualWiki> virtualWikis = WikiBase.getDataHandler().getVirtualWikiList();
			for (VirtualWiki virtualWiki : virtualWikis) {
				WikiDatabase.setupSpecialPage(request.getLocale(), virtualWiki.getName(), WikiBase.SPECIAL_PAGE_HEADER, wikiUser, true, false);
				this.renameSystemTopic(request, messages, virtualWiki.getName(), wikiUser, "BottomArea", WikiBase.SPECIAL_PAGE_FOOTER);
				this.renameSystemTopic(request, messages, virtualWiki.getName(), wikiUser, "LeftMenu", WikiBase.SPECIAL_PAGE_SIDEBAR);
				this.renameSystemTopic(request, messages, virtualWiki.getName(), wikiUser, "StyleSheet", WikiBase.SPECIAL_PAGE_SYSTEM_CSS);
				WikiDatabase.setupSpecialPage(request.getLocale(), virtualWiki.getName(), WikiBase.SPECIAL_PAGE_CUSTOM_CSS, wikiUser, true, false);
			}
			return true;
		} catch (WikiException e) {
			logger.warn("Failure while moving system topic", e);
			messages.add(e.getWikiMessage());
			messages.add(new WikiMessage("upgrade.error.fatal",  e.getMessage()));
			return false;
		} catch (DataAccessException e) {
			logger.warn("Failure while moving system topic", e);
			messages.add(new WikiMessage("upgrade.error.fatal",  e.getMessage()));
			return false;
		}
	}

	/**
	 *
	 */
	private boolean upgradeConfigXmlRequired() {
		WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
		return (oldVersion.before(1, 3, 0));
	}

	/**
	 *
	 */
	private boolean upgradeDatabase(boolean performUpgrade, List<WikiMessage> messages) throws WikiException {
		boolean upgradeRequired = false;
		WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
		if (oldVersion.before(1, 2, 0)) {
			upgradeRequired = true;
			if (performUpgrade) {
				DatabaseUpgrades.upgrade120(messages);
			}
		}
		if (oldVersion.before(1, 3, 0)) {
			upgradeRequired = true;
			if (performUpgrade) {
				DatabaseUpgrades.upgrade130(messages);
			}
		}
		// Flush connection pool to manage database schema change
		WikiDatabase.initialize();
		WikiCache.initialize();
		return upgradeRequired;
	}

	/**
	 * Utility method for rebuilding the search index if necessary.
	 */
	private boolean upgradeSearchIndex(boolean performUpgrade, List<WikiMessage> messages) {
		boolean upgradeRequired = false;
		WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
		if (oldVersion.before(1, 3, 0)) {
			upgradeRequired = true;
			if (performUpgrade) {
				if (oldVersion.before(1, 1, 0)) {
					try {
						int topicCount = WikiBase.getDataHandler().lookupTopicCount(VirtualWiki.defaultVirtualWiki().getName(), null);
						if (topicCount < MAX_TOPICS_FOR_AUTOMATIC_UPDATE) {
							// refresh search engine
							WikiBase.getSearchEngine().refreshIndex();
							messages.add(new WikiMessage("upgrade.message.search.refresh"));
						} else {
							// print a message telling the user to do this step manually
							WikiMessage searchWikiMessage = new WikiMessage("upgrade.error.search.refresh");
							searchWikiMessage.addWikiLinkParam("Special:Maintenance");
							messages.add(searchWikiMessage);
						}
					} catch (Exception e) {
						logger.warn("Failure during upgrade while rebuilding search index.  Please use the tools on the Special:Maintenance page to complete this step.", e);
						messages.add(new WikiMessage("upgrade.error.nonfatal", e.getMessage()));
					}
				}
			}
		}
		return upgradeRequired;
	}

	/**
	 *
	 */
	private boolean upgradeStyleSheet(HttpServletRequest request, List<WikiMessage> messages) {
		try {
			List<VirtualWiki> virtualWikis = WikiBase.getDataHandler().getVirtualWikiList();
			for (VirtualWiki virtualWiki : virtualWikis) {
				WikiBase.getDataHandler().updateSpecialPage(request.getLocale(), virtualWiki.getName(), WikiBase.SPECIAL_PAGE_SYSTEM_CSS, ServletUtil.getIpAddress(request));
				messages.add(new WikiMessage("upgrade.message.stylesheet.success", virtualWiki.getName()));
			}
			return true;
		} catch (WikiException e) {
			logger.warn("Failure while updating JAMWiki stylesheet", e);
			messages.add(e.getWikiMessage());
			messages.add(new WikiMessage("upgrade.message.stylesheet.failure",  e.getMessage()));
			return false;
		} catch (DataAccessException e) {
			logger.warn("Failure while updating JAMWiki stylesheet", e);
			messages.add(new WikiMessage("upgrade.message.stylesheet.failure",  e.getMessage()));
			return false;
		}
	}

	/**
	 *
	 */
	private boolean upgradeStyleSheetRequired() {
		WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
		return (oldVersion.before(1, 3, 0));
	}

	/**
	 *
	 */
	private void view(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) {
		WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
		if (oldVersion.before(1, 0, 0)) {
			pageInfo.addError(new WikiMessage("upgrade.error.oldversion", WikiVersion.CURRENT_WIKI_VERSION, "1.0.0"));
		}
		List<WikiMessage> upgradeDetails = new ArrayList<WikiMessage>();
		try {
			if (this.upgradeDatabase(false, null)) {
				upgradeDetails.add(new WikiMessage("upgrade.caption.database"));
			}
		} catch (Exception e) {
			// never thrown when the first parameter is false
		}
		if (this.upgradeSearchIndex(false, null)) {
			WikiMessage searchWikiMessage = new WikiMessage("upgrade.caption.search");
			searchWikiMessage.addParam(Integer.toString(MAX_TOPICS_FOR_AUTOMATIC_UPDATE));
			searchWikiMessage.addWikiLinkParam("Special:Maintenance");
			upgradeDetails.add(searchWikiMessage);
		}
		if (this.upgradeStyleSheetRequired()) {
			upgradeDetails.add(new WikiMessage("upgrade.caption.stylesheet"));
		}
		if (this.upgradeConfigXmlRequired()) {
			File file = null;
			try {
				file = WikiConfiguration.getInstance().retrieveConfigFile();
				upgradeDetails.add(new WikiMessage("upgrade.caption.config", file.getAbsolutePath()));
			} catch (IOException e) {
				logger.warn("Unable to retrieve configuration file location", e);
			}
		}
		upgradeDetails.add(new WikiMessage("upgrade.caption.releasenotes"));
		upgradeDetails.add(new WikiMessage("upgrade.caption.manual"));
		next.addObject("upgradeDetails", upgradeDetails);
		pageInfo.setContentJsp(JSP_UPGRADE);
		pageInfo.setSpecial(true);
	}
}
