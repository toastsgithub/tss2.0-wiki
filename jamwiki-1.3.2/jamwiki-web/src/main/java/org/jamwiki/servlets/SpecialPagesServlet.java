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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jamwiki.WikiMessage;
import org.jamwiki.utils.WikiLogger;
import org.springframework.web.servlet.ModelAndView;

/**
 * Used to generate an index of all Special: pages on the wiki.
 */
public class SpecialPagesServlet extends JAMWikiServlet {

	/** Logger for this class and subclasses. */
	private static final WikiLogger logger = WikiLogger.getLogger(SpecialPagesServlet.class.getName());
	/** The name of the JSP file used to render the servlet output. */
	protected static final String JSP_SPECIAL_PAGES = "all-special-pages.jsp";

	/**
	 * This method handles the request after its parent class receives control. It gets the topic's name and the
	 * virtual wiki name from the uri, loads the topic and returns a view to the end user.
	 *
	 * @param request - Standard HttpServletRequest object.
	 * @param response - Standard HttpServletResponse object.
	 * @return A <code>ModelAndView</code> object to be handled by the rest of the Spring framework.
	 */
	public ModelAndView handleJAMWikiRequest(HttpServletRequest request, HttpServletResponse response, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		specialPages(request, next, pageInfo);
		return next;
	}

	/**
	 *
	 */
	private void specialPages(HttpServletRequest request, ModelAndView next, WikiPageInfo pageInfo) throws Exception {
		pageInfo.setPageTitle(new WikiMessage("specialpages.title"));
		pageInfo.setContentJsp(JSP_SPECIAL_PAGES);
		pageInfo.setSpecial(true);
	}
}