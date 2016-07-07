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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jamwiki.DataAccessException;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiMessage;
import org.jamwiki.model.Namespace;
import org.jamwiki.model.TopicType;
import org.jamwiki.utils.Pagination;
import org.jamwiki.utils.WikiLogger;
import org.jamwiki.utils.WikiUtil;
import org.springframework.web.servlet.ModelAndView;

/**
 * Used to build Special: pages that display lists of topics on the Wiki, such
 * as when displaying lists of all existing images or topics.
 */
public class ItemsServlet extends JAMWikiServlet {

	/** Logger for this class and subclasses. */
	private static final WikiLogger logger = WikiLogger.getLogger(ItemsServlet.class.getName());
	/** The name of the JSP file used to render the servlet output. */
	protected static final String JSP_ITEMS = "items.jsp";

	/**
	 * This method handles the request after its parent class receives control. It gets the topic's name and the
	 * virtual wiki name from the uri, loads the topic and returns a view to the end user.
	 *
	 * @param request - Standard HttpServletRequest object.
	 * @param response - Standard HttpServletResponse object.
	 * @return A <code>ModelAndView</code> object to be handled by the rest of the Spring framework.
	 */
	public ModelAndView handleJAMWikiRequest(HttpServletRequest request, HttpServletResponse response, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		if (ServletUtil.isTopic(request, "Special:ImageList")) {
			viewImages(request, next, pageInfo);
		} else if (ServletUtil.isTopic(request, "Special:FileList")) {
			viewFiles(request, next, pageInfo);
		} else if (ServletUtil.isTopic(request, "Special:ListUsers")) {
			viewUsers(request, next, pageInfo);
		} else if (ServletUtil.isTopic(request, "Special:OrphanedPages")) {
			viewOrphanedPages(request, next, pageInfo);
		} else if (ServletUtil.isTopic(request, "Special:TopicsAdmin")) {
			viewTopicsAdmin(request, next, pageInfo);
		} else {
			viewTopics(request, next, pageInfo);
		}
		return next;
	}

	/**
	 *
	 */
	private void viewFiles(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String virtualWiki = pageInfo.getVirtualWikiName();
		Pagination pagination = ServletUtil.loadPagination(request, next);
		Map<Integer, String> items = WikiBase.getDataHandler().lookupTopicByType(virtualWiki, TopicType.FILE, TopicType.FILE, null, pagination);
		next.addObject("itemCount", items.size());
		next.addObject("items", items.values());
		next.addObject("rootUrl", "Special:FileList");
		if (items.isEmpty()) {
			pageInfo.addMessage(new WikiMessage("allfiles.message.none"));
		}
		pageInfo.setPageTitle(new WikiMessage("allfiles.title"));
		pageInfo.setContentJsp(JSP_ITEMS);
		pageInfo.setSpecial(true);
	}

	/**
	 *
	 */
	private void viewOrphanedPages(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String virtualWiki = pageInfo.getVirtualWikiName();
		Pagination pagination = ServletUtil.loadPagination(request, next);
		int namespaceId = (request.getParameter("namespace") == null) ? Namespace.MAIN_ID : Integer.valueOf(request.getParameter("namespace"));
		Set<String> allItems = new TreeSet<String>();
		allItems.addAll(WikiBase.getDataHandler().lookupTopicLinkOrphans(virtualWiki, namespaceId));
		List<String> items = Pagination.retrievePaginatedSubset(pagination, allItems);
		next.addObject("itemCount", items.size());
		next.addObject("items", items);
		if (items.isEmpty()) {
			pageInfo.addMessage(new WikiMessage("orphaned.message.none"));
		}
		String rootUrl = "Special:OrphanedPages";
		if (request.getParameter("namespace") != null) {
			rootUrl += "?namespace=" + namespaceId;
		}
		next.addObject("rootUrl", rootUrl);
		// add a map of namespace id & label for display on the front end.
		Map<Integer, String> namespaceMap = ServletUtil.loadNamespaceDisplayMap(virtualWiki, ServletUtil.retrieveUserLocale(request));
		next.addObject("namespaces", namespaceMap);
		pageInfo.setPageTitle(new WikiMessage("orphaned.title"));
		pageInfo.setContentJsp(JSP_ITEMS);
		pageInfo.setSpecial(true);
	}

	/**
	 *
	 */
	private void viewUsers(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String virtualWiki = pageInfo.getVirtualWikiName();
		Pagination pagination = ServletUtil.loadPagination(request, next);
		List<String> items = WikiBase.getDataHandler().lookupWikiUsers(pagination);
		List<String> links = new ArrayList<String>();
		for (String link : items) {
			links.add(Namespace.namespace(Namespace.USER_ID).getLabel(virtualWiki) + Namespace.SEPARATOR + link);
		}
		next.addObject("itemCount", items.size());
		next.addObject("items", links);
		next.addObject("rootUrl", "Special:ListUsers");
		if (items.isEmpty()) {
			pageInfo.addMessage(new WikiMessage("allusers.message.none"));
		}
		pageInfo.setPageTitle(new WikiMessage("allusers.title"));
		pageInfo.setContentJsp(JSP_ITEMS);
		pageInfo.setSpecial(true);
	}

	/**
	 *
	 */
	private void viewImages(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String virtualWiki = pageInfo.getVirtualWikiName();
		Pagination pagination = ServletUtil.loadPagination(request, next);
		Map<Integer, String> items = WikiBase.getDataHandler().lookupTopicByType(virtualWiki, TopicType.IMAGE, TopicType.IMAGE, null, pagination);
		next.addObject("itemCount", items.size());
		next.addObject("items", items.values());
		next.addObject("rootUrl", "Special:ImageList");
		if (items.isEmpty()) {
			pageInfo.addMessage(new WikiMessage("allimages.message.none"));
		}
		pageInfo.setPageTitle(new WikiMessage("allimages.title"));
		pageInfo.setContentJsp(JSP_ITEMS);
		pageInfo.setSpecial(true);
	}

	/**
	 *
	 */
	private void viewTopics(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws DataAccessException {
		String virtualWiki = pageInfo.getVirtualWikiName();
		Pagination pagination = ServletUtil.loadPagination(request, next);
		// find the current namespace and topic type
		int namespaceId = (request.getParameter("namespace") == null) ? Namespace.MAIN_ID : Integer.valueOf(request.getParameter("namespace"));
		TopicType topicType1 = (namespaceId == Namespace.FILE_ID) ? TopicType.FILE : TopicType.ARTICLE;
		TopicType topicType2 = WikiUtil.findTopicTypeForNamespace(WikiBase.getDataHandler().lookupNamespaceById(namespaceId));
		// retrieve a list of topics for the namespace
		Map<Integer, String> items = WikiBase.getDataHandler().lookupTopicByType(virtualWiki, topicType1, topicType2, namespaceId, pagination);
		next.addObject("itemCount", items.size());
		next.addObject("items", items.values());
		if (items.isEmpty()) {
			pageInfo.addMessage(new WikiMessage("alltopics.message.none"));
		}
		String rootUrl = "Special:AllPages";
		if (request.getParameter("namespace") != null) {
			rootUrl += "?namespace=" + namespaceId;
		}
		next.addObject("rootUrl", rootUrl);
		// add a map of namespace id & label for display on the front end.
		Map<Integer, String> namespaceMap = ServletUtil.loadNamespaceDisplayMap(virtualWiki, ServletUtil.retrieveUserLocale(request));
		next.addObject("namespaces", namespaceMap);
		pageInfo.setPageTitle(new WikiMessage("alltopics.title"));
		pageInfo.setContentJsp(JSP_ITEMS);
		pageInfo.setSpecial(true);
	}

	/**
	 *
	 */
	private void viewTopicsAdmin(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String virtualWiki = pageInfo.getVirtualWikiName();
		Pagination pagination = ServletUtil.loadPagination(request, next);
		List<String> items = WikiBase.getDataHandler().getTopicsAdmin(virtualWiki, pagination);
		next.addObject("itemCount", items.size());
		next.addObject("items", items);
		next.addObject("rootUrl", "Special:TopicsAdmin");
		if (items.isEmpty()) {
			pageInfo.addMessage(new WikiMessage("topicsadmin.message.none"));
		}
		pageInfo.setPageTitle(new WikiMessage("topicsadmin.title"));
		pageInfo.setContentJsp(JSP_ITEMS);
		pageInfo.setSpecial(true);
	}
}