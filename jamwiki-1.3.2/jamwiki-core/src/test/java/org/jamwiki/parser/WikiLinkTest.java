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
 *
 * Based on code generated by Agitar build: Agitator Version 1.0.2.000071 (Build date: Jan 12, 2007) [1.0.2.000071]
 */
package org.jamwiki.parser;

import org.jamwiki.JAMWikiUnitTest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jamwiki.model.Namespace;

/**
 *
 */
public class WikiLinkTest extends JAMWikiUnitTest {

	/**
	 *
	 */
	@Test
	public void testConstructor() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		assertNull("wikiLink.getQuery()", wikiLink.getQuery());
		assertNull("wikiLink.getSection()", wikiLink.getSection());
		assertNull("wikiLink.getText()", wikiLink.getText());
		assertNull("wikiLink.getArticle()", wikiLink.getArticle());
		assertEquals("wikiLink.getNamespace()", Namespace.namespace(Namespace.MAIN_ID), wikiLink.getNamespace());
		assertNull("wikiLink.getDestination()", wikiLink.getDestination());
		assertFalse("wikiLink.getColon()", wikiLink.getColon());
	}

	/**
	 *
	 */
	@Test
	public void testSetArticle() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setArticle("testWikiLinkArticle");
		assertEquals("wikiLink.getArticle()", "testWikiLinkArticle", wikiLink.getArticle());
	}

	/**
	 *
	 */
	@Test
	public void testSetColon() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setColon(true);
		assertTrue("wikiLink.getColon()", wikiLink.getColon());
	}

	/**
	 *
	 */
	@Test
	public void testSetDestination() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en", "testWikiLinkDestination");
		assertEquals("wikiLink.getDestination()", "testWikiLinkDestination", wikiLink.getDestination());
	}

	/**
	 *
	 */
	@Test
	public void testSetNamespace() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setNamespace(Namespace.namespace(Namespace.FILE_ID));
		assertEquals("wikiLink.getNamespace()", Namespace.namespace(Namespace.FILE_ID), wikiLink.getNamespace());
	}

	/**
	 *
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetNamespace2() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setNamespace(null);
		assertEquals("wikiLink.getNamespace()", Namespace.namespace(Namespace.FILE_ID), wikiLink.getNamespace());
	}

	/**
	 *
	 */
	@Test
	public void testSetQuery() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setQuery("testWikiLinkQuery");
		assertEquals("wikiLink.getQuery()", "testWikiLinkQuery", wikiLink.getQuery());
	}

	/**
	 *
	 */
	@Test
	public void testSetSection() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setSection("testWikiLinkSection");
		assertEquals("wikiLink.getSection()", "testWikiLinkSection", wikiLink.getSection());
	}

	/**
	 *
	 */
	@Test
	public void testSetText() throws Throwable {
		WikiLink wikiLink = new WikiLink("/wiki", "en");
		wikiLink.setText("testWikiLinkText");
		assertEquals("wikiLink.getText()", "testWikiLinkText", wikiLink.getText());
	}
}
