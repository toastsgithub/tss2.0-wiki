package org.jamwiki.servlets;

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

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.jamwiki.WikiException;
import org.jamwiki.WikiMessage;
import org.jamwiki.authentication.WikiUserDetailsImpl;
import org.jamwiki.model.Role;
import org.jamwiki.model.WikiUser;
import org.jamwiki.utils.TiddlyWikiParser;
import org.jamwiki.utils.WikiLogger;
import org.jamwiki.utils.WikiUtil;
import org.springframework.web.servlet.ModelAndView;

/**
 * Used to import an HTML file (in TiddlyWiki format), creating or updating a
 * topic as a result.
 *
 * @deprecated Tiddly wiki import support has been unmaintained since JAMWiki
 *  0.6.0 and will be removed in a future release unless a new maintainer is
 *  found.
 */
public class ImportTiddlyWikiServlet extends JAMWikiServlet {


	private static final WikiLogger logger = WikiLogger.getLogger(ImportTiddlyWikiServlet.class.getName());
	/** The name of the JSP file used to render the servlet output. */
	protected static final String JSP_IMPORT = "importtiddly.jsp";
        
        

	/**
	 * This method handles the request after its parent class receives control.
	 *
	 * @param request - Standard HttpServletRequest object.
	 * @param response - Standard HttpServletResponse object.
	 * @return A <code>ModelAndView</code> object to be handled by the rest of the Spring framework.
	 */
	public ModelAndView handleJAMWikiRequest(HttpServletRequest request, HttpServletResponse response, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String contentType = ((request.getContentType() == null) ? "" : request.getContentType().toLowerCase());
		if (contentType.indexOf("multipart") == -1) {
			view(request, next, pageInfo);
		} else {
			importFile(request, next, pageInfo);
		}
		return next;
	}

	/**
	 *
	 */
	private void importFile(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		String virtualWiki = pageInfo.getVirtualWikiName();
		List<FileItem> fileItems = ServletUtil.processMultipartRequest(request);
		WikiUserDetailsImpl userDetails = ServletUtil.currentUserDetails();
		WikiUser user = ServletUtil.currentWikiUser();
		if (userDetails.hasRole(Role.ROLE_ANONYMOUS)) {
			// FIXME - setting the user to null may not be necessary, but it is
			// consistent with how the code behaved when ServletUtil.currentUserDetails()
			// returned null for non-logged-in users
			user = null;
		}
		TiddlyWikiParser parser = new TiddlyWikiParser(virtualWiki, user, ServletUtil.getIpAddress(request));
		String topicName = null;
		for (FileItem fileItem : fileItems) {
			if (fileItem.isFormField()) {
				continue;
			}
			File xmlFile = saveFileItem(fileItem);
			topicName = parser.parse(xmlFile);
			xmlFile.delete();
		}
		if (!StringUtils.isBlank(topicName)) {
			ServletUtil.redirect(next, virtualWiki, topicName);
		} else {
			pageInfo.addError(new WikiMessage("import.caption.failure"));
			view(request, next, pageInfo);
		}
	}

	/**
	 *
	 */
	private void view(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		pageInfo.setContentJsp(JSP_IMPORT);
		pageInfo.setPageTitle(new WikiMessage("import.title"));
		pageInfo.setSpecial(true);
	}

	/**
	 *
	 */
	private File saveFileItem(FileItem item) throws Exception {
		// upload user file to the server
		File directory = WikiUtil.getTempDirectory();
		if (!directory.exists()) {
			throw new WikiException(new WikiMessage("upload.error.directorycreate",  directory.getAbsolutePath()));
		}
		// use current timestamp as unique file name
		String filename = System.currentTimeMillis() + ".xml";
		File xmlFile = new File(directory, filename);
		// transfer remote file
		item.write(xmlFile);
		return xmlFile;
	}
}


