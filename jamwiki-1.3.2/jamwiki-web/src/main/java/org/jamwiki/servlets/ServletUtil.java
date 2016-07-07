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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.Environment;
import org.jamwiki.JAMWikiParser;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiException;
import org.jamwiki.WikiMessage;
import org.jamwiki.authentication.JAMWikiAuthenticationConstants;
import org.jamwiki.authentication.WikiUserDetailsImpl;
import org.jamwiki.db.DatabaseConnection;
import org.jamwiki.model.Category;
import org.jamwiki.model.Namespace;
import org.jamwiki.model.Role;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicType;
import org.jamwiki.model.VirtualWiki;
import org.jamwiki.model.Watchlist;
import org.jamwiki.model.WikiFile;
import org.jamwiki.model.WikiFileVersion;
import org.jamwiki.model.WikiUser;
import org.jamwiki.parser.LinkUtil;
import org.jamwiki.parser.ParserException;
import org.jamwiki.parser.ParserInput;
import org.jamwiki.parser.ParserOutput;
import org.jamwiki.parser.ParserUtil;
import org.jamwiki.parser.WikiLink;
import org.jamwiki.utils.Encryption;
import org.jamwiki.utils.Pagination;
import org.jamwiki.utils.ResourceUtil;
import org.jamwiki.utils.SpamFilter;
import org.jamwiki.utils.Utilities;
import org.jamwiki.utils.WikiLogger;
import org.jamwiki.utils.WikiUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.servlet.ModelAndView;

/**
 * Utility methods useful when processing JAMWiki servlet requests.
 */
public class ServletUtil {

	private static final WikiLogger logger = WikiLogger.getLogger(ServletUtil.class.getName());
	/** The name of the output parameter used to store page information. */
	public static final String PARAMETER_PAGE_INFO = "pageInfo";
	/** The name of the output parameter used to store topic information. */
	public static final String PARAMETER_TOPIC_OBJECT = "topicObject";
	/** The name of the output parameter used to indicate that Spring should redirect to another servlet. */
	protected static final String SPRING_REDIRECT_PREFIX = "redirect:";

	/**
	 *
	 */
	private ServletUtil() {
	}

	/**
	 * Populate the virtualWikiLinks field of the pageInfo object for Special:
	 * pages.
	 */
	protected static void buildVirtualWikiLinks(HttpServletRequest request, WikiPageInfo pageInfo) throws DataAccessException {
		String topicName = WikiUtil.getTopicFromURI(request);
		WikiLink wikiLink = new WikiLink(request.getContextPath(), pageInfo.getVirtualWikiName(), topicName);
		List<String> virtualWikiLinks = new ArrayList<String>();
		List<VirtualWiki> virtualWikis = WikiBase.getDataHandler().getVirtualWikiList();
		for (VirtualWiki virtualWiki : virtualWikis) {
			if (StringUtils.equalsIgnoreCase(virtualWiki.getName(), pageInfo.getVirtualWikiName())) {
				continue;
			}
			String virtualWikiLink = wikiLink.getNamespace().getLabel(virtualWiki.getName()) + Namespace.SEPARATOR + wikiLink.getArticle();
			wikiLink = new WikiLink(request.getContextPath(), virtualWiki.getName(), virtualWikiLink);
			String text = virtualWiki.getName() + Namespace.SEPARATOR + wikiLink.getArticle();
			String url = LinkUtil.buildInternalLinkHtml(wikiLink, text, null, null, false);
			virtualWikiLinks.add(url);
		}
		pageInfo.setVirtualWikiLinks(virtualWikiLinks);
	}

	/**
	 * Retrieve the content of a topic from the cache, or if it is not yet in
	 * the cache then add it to the cache.
	 *
	 * @param context The servlet context for the topic being retrieved.  May
	 *  be <code>null</code> if the <code>cook</code> parameter is set to
	 *  <code>false</code>.
	 * @param locale The locale for the topic being retrieved.  May be
	 *  <code>null</code> if the <code>cook</code> parameter is set to
	 *  <code>false</code>.
	 * @param virtualWiki The virtual wiki for the topic being retrieved.
	 * @param topicName The name of the topic being retrieved.
	 * @param cook A parameter indicating whether or not the content should be
	 *  parsed before it is added to the cache.  Stylesheet content (CSS) is not
	 *  parsed, but most other content is parsed.
	 * @return The parsed or unparsed (depending on the <code>cook</code>
	 *  parameter) topic content.
	 */
	protected static String cachedContent(String context, Locale locale, String virtualWiki, String topicName, boolean cook) throws DataAccessException {
		String cacheKey = virtualWiki + '/' + topicName;
		String content = WikiBase.CACHE_PARSED_TOPIC_CONTENT.retrieveFromCache(cacheKey);
		if (content != null || WikiBase.CACHE_PARSED_TOPIC_CONTENT.isKeyInCache(cacheKey)) {
			return content;
		}
		try {
			Topic topic = WikiBase.getDataHandler().lookupTopic(virtualWiki, topicName, false);
			if (topic == null) {
				logger.warn("Missing system topic, this should be created to avoid errors: " + virtualWiki + " / " + topicName);
				return content;
			}
			content = topic.getTopicContent();
			if (cook) {
				ParserInput parserInput = new ParserInput(virtualWiki, topicName);
				parserInput.setAllowSectionEdit(false);
				parserInput.setAllowTableOfContents(false);
				parserInput.setContext(context);
				parserInput.setLocale(locale);
				content = ParserUtil.parse(parserInput, null, content);
			}
			WikiBase.CACHE_PARSED_TOPIC_CONTENT.addToCache(cacheKey, content);
		} catch (Exception e) {
			logger.warn("error getting cached page " + virtualWiki + " / " + topicName, e);
			return null;
		}
		return content;
	}

	/**
	 * This is a utility method that will check topic content for spam, and
	 * return <code>null</code> if no matching values are found, or if a spam
	 * pattern is found then that pattern will be returned.  It will also log
	 * information about the offending spam and user to the logs.
	 *
	 * @param request The current servlet request.
	 * @param identifier An identifier used in logging to specify where spam was found.
	 * @param args Any number of strings that should be validated againts the blacklist
	 *  to see if they contain spam.
	 * @return <code>null</code> if nothing in the topic content matches a current
	 *  spam pattern, or the text that matches a spam pattern if one is found.
	 */
	protected static String checkForSpam(HttpServletRequest request, String identifier, String... args) throws DataAccessException {
		// check the blacklist
		String result = null;
		String message = null;
		if (args != null) {
			for (String arg : args) {
				if (StringUtils.isBlank(arg)) {
					continue;
				}
				result = SpamFilter.containsSpam(arg);
				if (result != null) {
					message = "SPAM found in " + identifier;
					break;
				}
			}
		}
		// verify that the hidden input field is not populated
		String honeyPotInput = request.getParameter("jamAntispam");
		if (StringUtils.isBlank(result) && !StringUtils.isBlank(honeyPotInput)) {
			message = "SPAM honey pot triggered for " + identifier;
			result = (honeyPotInput.length() > 100) ? honeyPotInput.substring(0, 100) : honeyPotInput;
		}
		if (StringUtils.isBlank(result)) {
			return null;
		}
		message += " (";
		WikiUserDetailsImpl user = ServletUtil.currentUserDetails();
		if (!user.hasRole(Role.ROLE_ANONYMOUS)) {
			message += user.getUsername() + " / ";
		}
		message += ServletUtil.getIpAddress(request) + "): " + result;
		logger.info(message);
		return result;
	}

	/**
	 * Retrieve the current <code>WikiUserDetailsImpl</code> from Spring Security
	 * <code>SecurityContextHolder</code>.  If the current user is not
	 * logged-in then this method will return an empty <code>WikiUserDetailsImpl</code>
	 * object.
	 *
	 * @return The current logged-in <code>WikiUserDetailsImpl</code>, or an empty
	 *  <code>WikiUserDetailsImpl</code> if there is no user currently logged in.
	 *  This method will never return <code>null</code>.
	 * @throws AuthenticationCredentialsNotFoundException If authentication
	 *  credentials are unavailable.
	 */
	public static WikiUserDetailsImpl currentUserDetails() throws AuthenticationCredentialsNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return WikiUserDetailsImpl.initWikiUserDetailsImpl(auth);
	}

	/**
	 * Retrieve the current <code>WikiUser</code> using the <code>WikiUserDetailsImpl</code>
	 * from Spring Security <code>SecurityContextHolder</code>.  If there is no current
	 * user (the user is not logged in) then this method will return an empty WikiUser.
	 * The method will never return <code>null</code>.
	 *
	 * @return The current logged-in <code>WikiUser</code>, or an empty WikiUser if
	 *  there is no user currently logged in.
	 */
	public static WikiUser currentWikiUser() {
		WikiUserDetailsImpl userDetails = null;
		WikiUser user = new WikiUser(null);
		try {
			userDetails = ServletUtil.currentUserDetails();
		} catch (AuthenticationCredentialsNotFoundException e) {
			// TODO - occurs from the CSS page, figure out why.
			logger.debug("Unable to find authentication credentials for current user");
			return user;
		}
		String username = userDetails.getUsername();
		if (username.equals(WikiUserDetailsImpl.ANONYMOUS_USER_USERNAME)) {
			return user;
		}
		if (!WikiUtil.isFirstUse() && !WikiUtil.isUpgrade()) {
			try {
				// FIXME - do not lookup the user every time this method is called, that will kill performance
				user = WikiBase.getDataHandler().lookupWikiUser(username);
			} catch (DataAccessException e) {
				logger.error("Failure while retrieving user from database with login: " + username, e);
				return user;
			}
			if (user == null) {
				// invalid user.  someone has either spoofed a cookie or the user account is no longer in
				// the database.
				logger.warn("No user exists for principal found in security context authentication: " + username);
				SecurityContextHolder.clearContext();
				throw new AuthenticationCredentialsNotFoundException("Invalid user credentials found - username " + username + " does not exist in this wiki installation");
			}
		}
		return user;
	}

	/**
	 * Retrieve the current logged-in user's watchlist from the session.  If
	 * there is no watchlist return an empty watchlist.
	 *
	 * @param request The servlet request object.
	 * @param virtualWiki The virtual wiki for the watchlist being parsed.
	 * @return The current logged-in user's watchlist, or an empty watchlist
	 *  if there is no watchlist in the session.
	 * @throws WikiException Thrown if any error occurs during processing.
	 */
	public static Watchlist currentWatchlist(HttpServletRequest request, String virtualWiki) throws WikiException {
		// try to get watchlist stored in session
		if (request.getSession(false) != null) {
			Watchlist watchlist = (Watchlist)request.getSession(false).getAttribute(WikiUtil.PARAMETER_WATCHLIST);
			if (watchlist != null) {
				return watchlist;
			}
		}
		// no watchlist in session, retrieve from database
		WikiUserDetailsImpl userDetails = ServletUtil.currentUserDetails();
		Watchlist watchlist = new Watchlist();
		if (userDetails.hasRole(Role.ROLE_ANONYMOUS)) {
			return watchlist;
		}
		WikiUser user = ServletUtil.currentWikiUser();
		try {
			watchlist = WikiBase.getDataHandler().getWatchlist(virtualWiki, user.getUserId());
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		if (request.getSession(false) != null) {
			// add watchlist to session
			request.getSession(false).setAttribute(WikiUtil.PARAMETER_WATCHLIST, watchlist);
		}
		return watchlist;
	}

	/**
	 * Duplicate the functionality of the request.getRemoteAddr() method, but
	 * for IPv6 addresses strip off any local interface information (anything
	 * following a "%").
	 *
	 * @param request the HTTP request object.
	 * @return The IP address that the request originated from, or 0.0.0.0 if
	 *  the originating address cannot be determined.
	 */
	public static String getIpAddress(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request object cannot be null");
		}
		String ipAddress = request.getRemoteAddr();
		int pos = ipAddress.indexOf('%');
		if (pos != -1) {
			ipAddress = ipAddress.substring(0, pos);
		}
		if (!Utilities.isIpAddress(ipAddress)) {
			logger.info("Invalid IP address found in request: " + ipAddress);
			ipAddress = "0.0.0.0";
		}
		return ipAddress;
	}

	/**
	 * Initialize topic values for a Topic object.  This method will check to
	 * see if a topic with the specified name exists, and if it does exist
	 * then that topic will be returned.  Otherwise a new topic will be
	 * initialized, setting initial parameters such as topic name, virtual
	 * wiki, and topic type.
	 *
	 * @param virtualWiki The virtual wiki name for the topic being
	 *  initialized.
	 * @param topicName The name of the topic being initialized.
	 * @return A new topic object with basic fields initialized, or if a topic
	 *  with the given name already exists then the pre-existing topic is
	 *  returned.
	 * @throws WikiException Thrown if any error occurs while retrieving or
	 *  initializing the topic object.
	 */
	protected static Topic initializeTopic(String virtualWiki, String topicName) throws WikiException {
		LinkUtil.validateTopicName(virtualWiki, topicName, false);
		WikiLink wikiLink = new WikiLink(null, virtualWiki, topicName);
		Topic topic = null;
		try {
			topic = WikiBase.getDataHandler().lookupTopic(virtualWiki, wikiLink.getNamespace(), wikiLink.getArticle(), false);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		if (topic != null) {
			return topic;
		}
		topic = new Topic(virtualWiki, wikiLink.getNamespace(), wikiLink.getArticle());
		topic.setTopicType(WikiUtil.findTopicTypeForNamespace(wikiLink.getNamespace()));
		return topic;
	}

	/**
	 * Determine if a user has permission to edit a topic.
	 *
	 * @param virtualWiki The virtual wiki name for the topic in question.
	 * @param topicName The name of the topic in question.
	 * @param user The current Wiki user, or <code>null</code> if there is
	 *  no current user.
	 * @return <code>true</code> if the user is allowed to edit the topic,
	 *  <code>false</code> otherwise.
	 * @throws WikiException Thrown if any error occurs during processing.
	 */
	protected static boolean isEditable(String virtualWiki, String topicName, WikiUserDetailsImpl user) throws WikiException {
		if (user == null || !user.hasRole(Role.ROLE_EDIT_EXISTING)) {
			// user does not have appropriate permissions
			return false;
		}
		Topic topic = null;
		try {
			if (!user.hasRole(Role.ROLE_EDIT_NEW) && WikiBase.getDataHandler().lookupTopic(virtualWiki, topicName, false) == null) {
				// user does not have appropriate permissions
				return false;
			}
			topic = WikiBase.getDataHandler().lookupTopic(virtualWiki, topicName, false);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		if (topic == null) {
			// new topic, edit away...
			return true;
		}
		if (topic.getAdminOnly() && !user.hasRole(Role.ROLE_ADMIN)) {
			return false;
		}
		if (topic.getReadOnly()) {
			return false;
		}
		return true;
	}

	/**
	 * Determine if a user has permission to move a topic.
	 *
	 * @param virtualWiki The virtual wiki name for the topic in question.
	 * @param topicName The name of the topic in question.
	 * @param user The current Wiki user, or <code>null</code> if there is
	 *  no current user.
	 * @return <code>true</code> if the user is allowed to move the topic,
	 *  <code>false</code> otherwise.
	 * @throws WikiException Thrown if any error occurs during processing.
	 */
	protected static boolean isMoveable(String virtualWiki, String topicName, WikiUserDetailsImpl user) throws WikiException {
		if (user == null || !user.hasRole(Role.ROLE_MOVE)) {
			// no permission granted to move pages
			return false;
		}
		Topic topic = null;
		try {
			topic = WikiBase.getDataHandler().lookupTopic(virtualWiki, topicName, false);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		if (topic == null) {
			// cannot move a topic that doesn't exist
			return false;
		}
		if (topic.getReadOnly()) {
			return false;
		}
		if (topic.getAdminOnly() && !user.hasRole(Role.ROLE_ADMIN)) {
			return false;
		}
		return true;
	}

	/**
	 * Examine the request object, and see if the requested topic or page
	 * matches a given value.
	 *
	 * @param request The servlet request object.
	 * @param value The value to match against the current topic or page name.
	 * @return <code>true</code> if the value matches the current topic or
	 *  page name, <code>false</code> otherwise.
	 */
	protected static boolean isTopic(HttpServletRequest request, String value) {
		String topic = WikiUtil.getTopicFromURI(request);
		if (StringUtils.isBlank(topic)) {
			return false;
		}
		if (value != null && topic.equals(value)) {
			return true;
		}
		return false;
	}

	/**
	 * Utility method for adding categories associated with the current topic
	 * to the ModelAndView object.  This method adds a hashmap of category
	 * names and sort keys to the session that can then be retrieved for
	 * display during rendering.
	 *
	 * @param request The current servlet request object.
	 * @param next The current ModelAndView object used to return rendering
	 *  information.
	 * @param virtualWiki The virtual wiki name for the topic being rendered.
	 * @param topicName The name of the topic that is being rendered.
	 * @throws WikiException Thrown if any error occurs during processing.
	 */
	protected static void loadCategoryContent(HttpServletRequest request, ModelAndView next, String virtualWiki, String topicName) throws WikiException {
		String categoryName = topicName.substring(Namespace.namespace(Namespace.CATEGORY_ID).getLabel(virtualWiki).length() + Namespace.SEPARATOR.length());
		next.addObject("categoryName", categoryName);
		List<Category> categoryTopics = null;
		try {
			categoryTopics = WikiBase.getDataHandler().lookupCategoryTopics(virtualWiki, topicName);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		Pagination pagination = ServletUtil.loadPagination(request, next);
		List<Category> categoryImages = new ArrayList<Category>();
		LinkedHashMap<String, String> subCategories = new LinkedHashMap<String, String>();
		int i = 0;
		// loop through the results and split out images and sub-categories
		while (i < categoryTopics.size()) {
			Category category = categoryTopics.get(i);
			if (category.getTopicType() == TopicType.IMAGE) {
				categoryTopics.remove(i);
				categoryImages.add(category);
				continue;
			}
			if (category.getTopicType() == TopicType.CATEGORY) {
				categoryTopics.remove(i);
				String value = category.getChildTopicName().substring(Namespace.namespace(Namespace.CATEGORY_ID).getLabel(virtualWiki).length() + Namespace.SEPARATOR.length());
				subCategories.put(category.getChildTopicName(), value);
				continue;
			}
			i++;
		}
		// manually process pagination
		List<Category> paginatedCategories = Pagination.retrievePaginatedSubset(pagination, categoryTopics);
		next.addObject("categoryTopics", paginatedCategories);
		next.addObject("numCategoryTopics", categoryTopics.size());
		next.addObject("categoryImages", categoryImages);
		next.addObject("numCategoryImages", categoryImages.size());
		next.addObject("subCategories", subCategories);
		next.addObject("numSubCategories", subCategories.size());
		next.addObject("displayCategoryCount", paginatedCategories.size());
	}

	/**
	 * Generate a map of key-value pairs consisting of a namespace ID and a
	 * display label, suitable for use in drop-downs or other namepsace
	 * selection tools on the front-end.  This method will automatically
	 * filter out internal namespaces (ie those with ID < 0).
	 *
	 * @param virtualWiki The virtual wiki for the namespace map being
	 *  retrieved.
	 * @param locale The user's current locale, used to format a message key
	 *  for the default namespace.
	 * @return A map of namespace ID - display value for use on the front end.
	 * @throws DataAccessException Thrown if an error occurs while retrieving
	 *  namespace data.
	 */
	protected static Map<Integer, String> loadNamespaceDisplayMap(String virtualWiki, Locale locale) throws DataAccessException {
		List<Namespace> namespaces = WikiBase.getDataHandler().lookupNamespaces();
		Map<Integer, String> namespaceMap = new TreeMap<Integer, String>();
		for (Namespace namespace : namespaces) {
			if (namespace.getId() < 0) {
				continue;
			}
			String label = (namespace.getId() == Namespace.MAIN_ID) ? Utilities.formatMessage("common.namespace.main", locale) : namespace.getLabel(virtualWiki);
			namespaceMap.put(namespace.getId(), label);
		}
		return namespaceMap;
	}

	/**
	 * Create a Pagination object and load all necessary values into the
	 * request for processing by a JSP.
	 *
	 * @param request The servlet request object.
	 * @param next A ModelAndView object corresponding to the page being
	 *  constructed.
	 * @return A Pagination object constructed from parameters found in the
	 *  request object.
	 */
	public static Pagination loadPagination(HttpServletRequest request, ModelAndView next) {
		if (next == null) {
			throw new IllegalArgumentException("A non-null ModelAndView object must be specified when loading pagination values");
		}
		Pagination pagination = WikiUtil.buildPagination(request);
		next.addObject("num", pagination.getNumResults());
		next.addObject("offset", pagination.getOffset());
		return pagination;
	}

	/**
	 * Utility method for parsing a multipart servlet request.  This method returns
	 * a list of FileItem objects that corresponds to the request.
	 *
	 * @param request The servlet request containing the multipart request.
	 * @return Returns a list of FileItem objects the corresponds to the request.
	 * @throws WikiException Thrown if any problems occur while processing the request.
	 */
	public static List<FileItem> processMultipartRequest(HttpServletRequest request) throws WikiException {
		File uploadDirectory = WikiUtil.getTempDirectory();
		if (!uploadDirectory.exists()) {
			throw new WikiException(new WikiMessage("upload.error.directorycreate", uploadDirectory.getAbsolutePath()));
		}
		long maxFileSize = Environment.getLongValue(Environment.PROP_FILE_MAX_FILE_SIZE);
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(uploadDirectory);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		upload.setSizeMax(maxFileSize);
		try {
			return (List<FileItem>)upload.parseRequest(request);
		} catch (FileUploadException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
	}

	/**
	 * Modify the current ModelAndView object to create a Spring redirect
	 * response, meaning that the view name becomes "redirect:" followed by
	 * the redirection target.
	 *
	 * @param next The current ModelAndView object, which will be reset by
	 *  this method.
	 * @param virtualWiki The virtual wiki name for the page being redirected
	 *  to.
	 * @param destination The topic or page name that is the redirection
	 *  target.  An example might be "Special:Login".
	 * @throws WikiException Thrown if any error occurs while processing.
	 */
	protected static void redirect(ModelAndView next, String virtualWiki, String destination) throws WikiException {
		String target = null;
		// set null context path since this is a redirect within the servlet context
		WikiLink wikiLink = LinkUtil.parseWikiLink(null, virtualWiki, destination);
		try {
			target = LinkUtil.buildTopicUrl(wikiLink);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		String view = ServletUtil.SPRING_REDIRECT_PREFIX + target;
		next.clear();
		next.setViewName(view);
	}

	/**
	 * Generate a target URL for redirection after login if required by
	 * Spring Security.
	 */
	private static String retrieveLoginTargetUrl(HttpServletRequest request, String virtualWikiName, String topic) {
		String target = request.getParameter(JAMWikiAuthenticationConstants.SPRING_SECURITY_LOGIN_TARGET_URL_FIELD_NAME);
		if (!StringUtils.isBlank(target) && !target.startsWith("/")) {
			// Link hijacking is possible with a link such as
			// Special:Login?spring-security-redirect=http://www.google.com
			String message = "Possible link hijacking attempt from " + ServletUtil.getIpAddress(request);
			message += " / request URL: " + request.getRequestURL();
			if (!StringUtils.isBlank(request.getQueryString())) {
				message += "?" + Utilities.getQueryString(request);
			}
			logger.warn(message);
			return null;
		}
		if (StringUtils.isBlank(target)) {
			if (StringUtils.isBlank(topic)) {
				VirtualWiki virtualWiki = ServletUtil.retrieveVirtualWiki(virtualWikiName);
				topic = virtualWiki.getRootTopicName();
			}
			target = "/" + virtualWikiName + "/" + topic;
			if (!StringUtils.isBlank(request.getQueryString())) {
				target += "?" + Utilities.getQueryString(request);
			}
		}
		return target;
	}

	/**
	 * Users can specify a default locale in their preferences, so determine
	 * if the current user is logged-in and has chosen a locale.  If not, use
	 * the default locale from the request object.
	 *
	 * @param request The request object for the HTTP request.
	 * @return Either the user's default locale (for logged-in users) or the
	 *  locale specified in the request if no default locale is available.
	 */
	public static Locale retrieveUserLocale(HttpServletRequest request) {
		try {
			WikiUser user = ServletUtil.currentWikiUser();
			if (user.getDefaultLocale() != null) {
				return LocaleUtils.toLocale(user.getDefaultLocale());
			}
		} catch (AuthenticationCredentialsNotFoundException e) {
			// ignore
		}
		return request.getLocale();
	}

	/**
	 * Given a virtual wiki name, return a <code>VirtualWiki</code> object.
	 * If there is no virtual wiki available with the given name then the
	 * default virtual wiki is returned.
	 *
	 * @param virtualWikiName The name of the virtual wiki that is being
	 *  retrieved.
	 * @return A <code>VirtualWiki</code> object.  If there is no virtual
	 *  wiki available with the given name then the default virtual wiki is
	 *  returned.
	 */
	public static VirtualWiki retrieveVirtualWiki(String virtualWikiName) {
		VirtualWiki virtualWiki = null;
		if (virtualWikiName == null) {
			virtualWikiName = VirtualWiki.defaultVirtualWiki().getName();
		}
		// FIXME - the check here for initialized properties is due to this
		// change being made late in a release cycle.  Revisit in a future
		// release & clean this up.
		if (Environment.isInitialized()) {
			try {
				virtualWiki = WikiBase.getDataHandler().lookupVirtualWiki(virtualWikiName);
			} catch (DataAccessException e) {}
		}
		if (virtualWiki == null) {
			logger.error("No virtual wiki found for " + virtualWikiName);
			virtualWiki = VirtualWiki.defaultVirtualWiki();
		}
		return virtualWiki;
	}

	/**
	 * Generate a ParserInput object appropriate for the given topic parameters.
	 */
	private static ParserInput topicParserInput(HttpServletRequest request, Topic topic, boolean sectionEdit) throws WikiException {
		WikiUserDetailsImpl userDetails = ServletUtil.currentUserDetails();
		if (sectionEdit && !ServletUtil.isEditable(topic.getVirtualWiki(), topic.getName(), userDetails)) {
			sectionEdit = false;
		}
		WikiUser user = ServletUtil.currentWikiUser();
		ParserInput parserInput = new ParserInput(topic.getVirtualWiki(), topic.getName());
		parserInput.setContext(request.getContextPath());
		parserInput.setLocale(request.getLocale());
		parserInput.setWikiUser(user);
		parserInput.setUserDisplay(ServletUtil.getIpAddress(request));
		parserInput.setAllowSectionEdit(sectionEdit);
		return parserInput;
	}

	/**
	 * Validate that vital system properties, such as database connection settings,
	 * have been specified properly.
	 *
	 * @param props The property object to validate against.
	 * @return A list of WikiMessage objects containing any errors encountered,
	 *  or an empty list if no errors are encountered.
	 */
	protected static List<WikiMessage> validateSystemSettings(Properties props) {
		List<WikiMessage> errors = new ArrayList<WikiMessage>();
		// test directory permissions & existence
		WikiMessage baseDirError = WikiUtil.validateDirectory(props.getProperty(Environment.PROP_BASE_FILE_DIR));
		if (baseDirError != null) {
			errors.add(baseDirError);
		}
		if (props.getProperty(Environment.PROP_FILE_UPLOAD_STORAGE).equals(WikiBase.UPLOAD_STORAGE.DOCROOT.toString())) {
			WikiMessage fullDirError = WikiUtil.validateDirectory(props.getProperty(Environment.PROP_FILE_DIR_FULL_PATH));
			if (fullDirError != null) {
				errors.add(fullDirError);
			}
		}
		String classesDir = null;
		try {
			classesDir = ResourceUtil.getClassLoaderRoot().getPath();
			WikiMessage classesDirError = WikiUtil.validateDirectory(classesDir);
			if (classesDirError != null) {
				errors.add(classesDirError);
			}
		} catch (IOException e) {
			errors.add(new WikiMessage("error.directorywrite", classesDir, e.getMessage()));
		}
		// test database
		String driver = props.getProperty(Environment.PROP_DB_DRIVER);
		String url = props.getProperty(Environment.PROP_DB_URL);
		String userName = props.getProperty(Environment.PROP_DB_USERNAME);
		String password = Encryption.getEncryptedProperty(Environment.PROP_DB_PASSWORD, props);
		try {
			DatabaseConnection.testDatabase(driver, url, userName, password, false);
		} catch (ClassNotFoundException e) {
			logger.error("Invalid database settings", e);
			errors.add(new WikiMessage("error.databaseconnection", e.getMessage()));
		} catch (SQLException e) {
			logger.error("Invalid database settings", e);
			errors.add(new WikiMessage("error.databaseconnection", e.getMessage()));
		}
		// verify valid parser class
		String parserClass = props.getProperty(Environment.PROP_PARSER_CLASS);
		String abstractParserClass = JAMWikiParser.class.getName();
		boolean validParser = (parserClass != null && !parserClass.equals(abstractParserClass));
		if (validParser) {
			try {
				Class parent = ClassUtils.getClass(parserClass);
				Class child = ClassUtils.getClass(abstractParserClass);
				if (!child.isAssignableFrom(parent)) {
					validParser = false;
				}
			} catch (ClassNotFoundException e) {
				validParser = false;
			}
		}
		if (!validParser) {
			errors.add(new WikiMessage("error.parserclass", parserClass));
		}
		return errors;
	}

	/**
	 * Utility method used when redirecting to a login page.
	 *
	 * @param request The servlet request object.
	 * @param pageInfo The current WikiPageInfo object, which contains
	 *  information needed for rendering the final JSP page.
	 * @param topic The topic to be redirected to.  Valid examples are
	 *  "Special:Admin", "StartingPoints", etc.
	 * @param messageObject A WikiMessage object to be displayed on the login
	 *  page.
	 * @return Returns a ModelAndView object corresponding to the login page
	 *  display.
	 * @throws WikiException Thrown if any error occurs during processing.
	 */
	protected static ModelAndView viewLogin(HttpServletRequest request, WikiPageInfo pageInfo, String topic, WikiMessage messageObject) throws WikiException {
		ModelAndView next = new ModelAndView("wiki");
		pageInfo.reset();
		String virtualWikiName = pageInfo.getVirtualWikiName();
		next.addObject("springSecurityTargetUrlField", JAMWikiAuthenticationConstants.SPRING_SECURITY_LOGIN_TARGET_URL_FIELD_NAME);
		HttpSession session = request.getSession(false);
		HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
		if (request.getRequestURL().indexOf(request.getRequestURI()) != -1 && httpSessionRequestCache.getRequest(request, null) == null) {
			// Only add a target URL if Spring Security has not saved a request in the session.  The request
			// URL vs URI check is needed due to the fact that the first time a user is redirected by Spring
			// Security to the login page the saved request attribute is not yet available in the session
			// due to weirdness and magic which I've thus far been unable to track down, so comparing the URI
			// to the URL provides a way of determining if the user was redirected.  Anyone who can create
			// a check that reliably captures whether or not Spring Security has a saved request should
			// feel free to modify the conditional above.
			String target = ServletUtil.retrieveLoginTargetUrl(request, virtualWikiName, topic);
			if (target != null) {
				next.addObject("springSecurityTargetUrl", target);
			}
		}
		String springSecurityLoginUrl = "/" + virtualWikiName + JAMWikiAuthenticationConstants.SPRING_SECURITY_LOGIN_URL;
		next.addObject("springSecurityLoginUrl", springSecurityLoginUrl);
		next.addObject("springSecurityUsernameField", JAMWikiAuthenticationConstants.SPRING_SECURITY_LOGIN_USERNAME_FIELD_NAME);
		next.addObject("springSecurityPasswordField", JAMWikiAuthenticationConstants.SPRING_SECURITY_LOGIN_PASSWORD_FIELD_NAME);
		next.addObject("springSecurityRememberMeField", JAMWikiAuthenticationConstants.SPRING_SECURITY_LOGIN_REMEMBER_ME_FIELD_NAME);
		pageInfo.setPageTitle(new WikiMessage("login.title"));
		pageInfo.setContentJsp(JAMWikiServlet.JSP_LOGIN);
		pageInfo.setSpecial(true);
		if (messageObject != null) {
			next.addObject("messageObject", messageObject);
		}
		if (WikiUtil.isUpgrade()) {
			next.addObject("upgradeInProgress", true);
		}
		return next;
	}

	/**
	 * Utility method used when viewing a topic.
	 *
	 * @param request The current servlet request object.
	 * @param next The current Spring ModelAndView object.
	 * @param pageInfo The current WikiPageInfo object, which contains
	 *  information needed for rendering the final JSP page.
	 * @param pageTitle A WikiMessage for the title of the page being rendered.  The
	 *  first parameter of the message should be the topic name.
	 * @param topic The Topic object for the topic being displayed.
	 * @param sectionEdit Set to <code>true</code> if edit links should be displayed
	 *  for each section of the topic.
	 * @param allowRedirect Setting this parameter to <code>true</code> will force the
	 *  redirection target to be displayed (rather than a redirect page) if the topic is a
	 *  redirect.
	 * @throws WikiException Thrown if any error occurs while retrieving or parsing the topic.
	 */
	protected static void viewTopic(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo, WikiMessage pageTitle, Topic topic, boolean sectionEdit, boolean allowRedirect) throws WikiException {
		// FIXME - what should the default be for topics that don't exist?
		if (topic == null) {
			throw new WikiException(new WikiMessage("common.exception.notopic"));
		}
		LinkUtil.validateTopicName(topic.getVirtualWiki(), topic.getName(), false);
		if (allowRedirect && topic.getTopicType() == TopicType.REDIRECT && (request.getParameter("redirect") == null || !request.getParameter("redirect").equalsIgnoreCase("no"))) {
			topic = ServletUtil.viewTopicRedirect(request, pageInfo, pageTitle, topic);
		}
		ParserInput parserInput = ServletUtil.topicParserInput(request, topic, sectionEdit);
		ParserOutput parserOutput = new ParserOutput();
		String content = null;
		try {
			content = ParserUtil.parse(parserInput, parserOutput, topic.getTopicContent());
		} catch (ParserException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		if (parserOutput.getCategories().size() > 0) {
			LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();
			for (String key : parserOutput.getCategories().keySet()) {
				String value = key.substring(Namespace.namespace(Namespace.CATEGORY_ID).getLabel(topic.getVirtualWiki()).length() + Namespace.SEPARATOR.length());
				categories.put(key, value);
			}
			next.addObject("categories", categories);
		}
		topic.setTopicContent(content);
		if (topic.getTopicType() == TopicType.CATEGORY) {
			loadCategoryContent(request, next, topic.getVirtualWiki(), topic.getName());
		}
		pageInfo.setInterwikiLinks(parserOutput.getInterwikiLinks());
		pageInfo.setVirtualWikiLinks(parserOutput.getVirtualWikiLinks());
		if (topic.getTopicType() == TopicType.IMAGE || topic.getTopicType() == TopicType.FILE) {
			ServletUtil.viewTopicImage(request, next, pageInfo, topic);
		}
		pageInfo.setSpecial(false);
		pageInfo.setTopicName(topic.getName());
		next.addObject(ServletUtil.PARAMETER_TOPIC_OBJECT, topic);
		if (pageTitle != null) {
			if (parserOutput.getPageTitle() != null) {
				pageTitle.replaceParameter(0, parserOutput.getPageTitle());
			}
			pageInfo.setPageTitle(pageTitle);
		}
	}

	/**
	 * If a topic is an image then set metadata appropriately.
	 */
	private static void viewTopicImage(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo, Topic topic) throws WikiException {
		WikiFile wikiFile = null;
		List<WikiFileVersion> fileVersions = null;
		try {
			wikiFile = WikiBase.getDataHandler().lookupWikiFile(topic.getVirtualWiki(), topic.getName());
			fileVersions = WikiBase.getDataHandler().getAllWikiFileVersions(topic.getVirtualWiki(), topic.getName(), true);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		WikiUser wikiUser;
		for (WikiFileVersion fileVersion : fileVersions) {
			// make sure the authorDisplay field is equal to the login for non-anonymous uploads
			if (fileVersion.getAuthorId() != null) {
				try {
					wikiUser = WikiBase.getDataHandler().lookupWikiUser(fileVersion.getAuthorId());
				} catch (DataAccessException e) {
					throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
				}
				if (wikiUser != null) {
					// wikiUser should never be null unless the data in the database is somehow corrupt
					fileVersion.setAuthorDisplay(wikiUser.getUsername());
				}
			}
		}
		next.addObject("fileVersions", fileVersions);
		if (topic.getTopicType() == TopicType.IMAGE) {
			next.addObject("topicImage", true);
		} else {
			next.addObject("topicFile", true);
		}
		// use the WikiFile virtual wiki rather than the topic to work around
		// a corner case where there could be an image page topic created for
		// the virtual wiki even though the actual image file is only on the
		// shared virtual wiki.
		boolean sharedImage = !pageInfo.getVirtualWikiName().equals(wikiFile.getVirtualWiki());
		if (sharedImage) {
			try {
				Topic sharedImageTopic = topic;
				if (!StringUtils.equals(wikiFile.getVirtualWiki(), topic.getVirtualWiki())) {
					// look up the shared topic file
					sharedImageTopic = WikiBase.getDataHandler().lookupTopicById(wikiFile.getTopicId());
				}
				WikiLink wikiLink = new WikiLink(request.getContextPath(), sharedImageTopic.getVirtualWiki(), sharedImageTopic.getName());
				pageInfo.setCanonicalUrl(wikiLink.toRelativeUrl());
				next.addObject("sharedImageTopicObject", sharedImageTopic);
			} catch (DataAccessException e) {
				throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
			}
		}
	}

	/**
	 * If a topic is a redirect then set metadata appropriately, and return
	 * the topic that is the target of the redirection.
	 */
	private static Topic viewTopicRedirect(HttpServletRequest request, WikiPageInfo pageInfo, WikiMessage pageTitle, Topic topic) throws WikiException {
		Topic child = null;
		try {
			child = LinkUtil.findRedirectedTopic(topic, 0);
		} catch (DataAccessException e) {
			throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
		}
		if (!child.getName().equals(topic.getName())) {
			String redirectUrl = null;
			WikiLink wikiLink = new WikiLink(request.getContextPath(), topic.getVirtualWiki(), topic.getName());
			try {
				redirectUrl = LinkUtil.buildTopicUrl(wikiLink);
			} catch (DataAccessException e) {
				throw new WikiException(new WikiMessage("error.unknown", e.getMessage()), e);
			}
			// FIXME - hard coding
			redirectUrl += LinkUtil.appendQueryParam("", "redirect", "no");
			String redirectName = topic.getName();
			pageInfo.setRedirectInfo(redirectUrl, redirectName);
			pageTitle.replaceParameter(0, child.getName());
			topic = child;
			wikiLink = new WikiLink(request.getContextPath(), topic.getVirtualWiki(), topic.getName());
			pageInfo.setCanonicalUrl(wikiLink.toRelativeUrl());
			// update the page info's virtual wiki in case this redirect is to another virtual wiki
			pageInfo.setVirtualWikiName(topic.getVirtualWiki());
		}
		return topic;
	}

	/**
	 * Utility method used when viewing a topic's source.  This functionality is
	 * primarily used when a user does not have permission to edit a topic or if
	 * a deleted topic version is being viewed.
	 *
	 * @param next The current Spring ModelAndView object.
	 * @param pageInfo The current WikiPageInfo object, which contains
	 *  information needed for rendering the final JSP page.
	 * @param topic The topic being viewed.
	 */
	protected static void viewTopicSource(ModelAndView next, WikiPageInfo pageInfo, Topic topic) {
		next.addObject("contents", topic.getTopicContent());
		pageInfo.setPageTitle(new WikiMessage("viewsource.title", topic.getName()));
		pageInfo.setTopicName(topic.getName());
		pageInfo.setContentJsp(JAMWikiServlet.JSP_VIEW_SOURCE);
		if (topic.getDeleted()) {
			pageInfo.setSpecial(true);
		}
	}
}
