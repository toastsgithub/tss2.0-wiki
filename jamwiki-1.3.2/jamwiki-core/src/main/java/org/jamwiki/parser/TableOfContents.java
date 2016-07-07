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
package org.jamwiki.parser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jamwiki.Environment;
import org.jamwiki.utils.Utilities;
import org.jamwiki.utils.WikiUtil;

/**
 * This class is used to generate a table of contents based on values passed in
 * through the parser.
 */
public class TableOfContents {

	/**
	 * Status indicating that this TOC object has not yet been initialized.  For the JFlex parser
	 * this will mean no __TOC__ tag has been added to the document being parsed.
	 */
	public static final int STATUS_TOC_UNINITIALIZED = 0;
	/**
	 * Status indicating that this TOC object has been initialized.  For the JFlex parser this
	 * will mean a __TOC__ tag has been added to the document being parsed.
	 */
	public static final int STATUS_TOC_INITIALIZED = 1;
	/** Status indicating that the document being parsed does not allow a table of contents. */
	public static final int STATUS_NO_TOC = 2;
	/** Path to the template used to format the TOC container, relative to the classpath. */
	private static final String TEMPLATE_TOC_CONTAINER = "templates/toc-wrapper.template";
	/** Path to the template used to format a TOC entry, relative to the classpath. */
	private static final String TEMPLATE_TOC_ENTRY = "templates/toc-entry.template";
	/** Force a TOC to appear */
	private boolean forceTOC = false;
	/** It is possible for a user to include more than one "TOC" tag in a document, so keep count. */
	private int insertTagCount = 0;
	/** Keep track of how many times the parser attempts to insert the TOC (one per "TOC" tag) */
	private int insertionAttempt = 0;
	/**
	 * minLevel holds the minimum TOC heading level that is being displayed by the current TOC.  For
	 * example, if the TOC contains only h3 and h4 entries, this value would be 3.
	 */
	private int minLevel = 4;
	private Map<String, TableOfContentsEntry> entries;
	private int status = STATUS_TOC_UNINITIALIZED;
	/** The minimum number of headings that must be present for a TOC to appear, unless forceTOC is set to true. */
	private static final int MINIMUM_HEADINGS = 4;
	/**
	 * Keep track of the TOC prefix to display.  This array is initialized with all ones, and each element
	 * is then incremented as the TOC is displayed.
	 */
	private int[] tocPrefixes;

	/**
	 * Add a new table of contents entry to the table of contents object.
	 * The entry should contain the name to use in the HTML anchor tag,
	 * the text to display in the table of contents, and the indentation
	 * level for the entry within the table of contents.
	 *
	 * @param name The name of the entry, to be used in the anchor tag name.
	 * @param text The text to display for the table of contents entry.
	 * @param level The level of the entry.  If an entry is a sub-heading of
	 *  another entry the value should be 2.  If there is a sub-heading of that
	 *  entry then its value would be 3, and so forth.
	 */
	public void addEntry(String name, String text, int level) {
		if (this.status != STATUS_NO_TOC && this.status != STATUS_TOC_INITIALIZED) {
			this.setStatus(STATUS_TOC_INITIALIZED);
		}
		name = this.buildUniqueName(name);
		TableOfContentsEntry entry = new TableOfContentsEntry(name, text, level);
		this.getEntries().put(name, entry);
		if (level < minLevel) {
			minLevel = level;
		}
	}

	/**
	 * This method checks to see if a TOC is allowed to be inserted, and if so
	 * returns an HTML representation of the TOC.
	 *
	 * @param parserInput The current parser input configuration object.
	 * @return An HTML representation of the current table of contents object,
	 *  or an empty string if the table of contents can not be inserted due
	 *  to an inadequate number of entries or some other reason.
	 */
	public String attemptTOCInsertion(ParserInput parserInput, String raw) throws IOException {
		// if a TOC is not inserted make sure that any matched newlines are returned
		String emptyResult = "";
		for (int i = 0; i < StringUtils.countMatches(raw, "\n"); i++) {
			emptyResult += '\n';
		}
		this.insertionAttempt++;
		if (!parserInput.getAllowTableOfContents()) {
			// TOC forbidden due to configuration
			return emptyResult;
		}
		if (this.size() == 0 || (this.size() < MINIMUM_HEADINGS && !this.forceTOC)) {
			// too few headings
			return emptyResult;
		}
		if (this.getStatus() == TableOfContents.STATUS_NO_TOC) {
			// TOC disallowed
			return emptyResult;
		}
		if (!Environment.getBooleanValue(Environment.PROP_PARSER_TOC)) {
			// TOC turned off for the wiki
			return emptyResult;
		}
		if (this.insertionAttempt < this.insertTagCount) {
			// user specified a TOC location, only insert there
			return emptyResult;
		}
		return '\n' + this.toHTML(parserInput.getLocale()) + '\n';
	}

	/**
	 * Verify the the TOC name is unique.  If it is already in use append
	 * a numerical suffix onto it.
	 *
	 * @param name The name to use in the TOC, unless it is already in use.
	 * @return A unique name for use in the TOC, of the form "name" or "name_1"
	 *  if "name" is already in use.
	 */
	public String buildUniqueName(String name) {
		if (StringUtils.isBlank(name)) {
			name = "empty";
		}
		int count = 0;
		// ensure that all characters in the name are valid for use in an anchor name
		String escapedName = LinkUtil.buildAnchorText(name);
		String candidate = escapedName;
		while (count < 1000) {
			if (this.getEntries().get(candidate) == null) {
				break;
			}
			count++;
			candidate = escapedName + "_" + count;
		}
		return candidate;
	}

	/**
	 * Internal method to close any list tags prior to adding the next entry.
	 */
	private void closeList(int level, StringBuilder text, int previousLevel) {
		for (int i = previousLevel; i > level; i--) {
			// close lists to current level
			text.append("</li>\n</ul>");
		}
	}

	/**
	 * Return the mapping of TOC name to entry, never <code>null</code>.
	 */
	private Map<String, TableOfContentsEntry> getEntries() {
		if (this.entries == null) {
			this.entries = new LinkedHashMap<String, TableOfContentsEntry>();
		}
		return this.entries;
	}

	/**
	 * Return the current table of contents status, such as "no table of contents
	 * allowed" or "uninitialized".
	 *
	 * @return The current status of this table of contents object.
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 *
	 */
	private String nextTocPrefix(int depth) {
		// initialize the tocPrefixes value for display
		int maxDepth = Environment.getIntValue(Environment.PROP_PARSER_TOC_DEPTH);
		if (this.tocPrefixes == null) {
			// initialize the prefix array
			this.tocPrefixes = new int[maxDepth];
			for (int i = 0; i < maxDepth; i++) {
				this.tocPrefixes[i] = 0;
			}
		}
		// increment current element
		this.tocPrefixes[depth] = this.tocPrefixes[depth] + 1;
		// clear out all lower elements
		for (int i = depth + 1; i < maxDepth; i++) {
			this.tocPrefixes[i] = 0;
		}
		// generate next prefix of the form 1.1.1
		String prefix = Integer.valueOf(this.tocPrefixes[0]).toString();
		for (int i = 1; i <= depth; i++) {
			prefix += "." + this.tocPrefixes[i];
		}
		return prefix;
	}

	/**
	 * Internal method to open any list tags prior to adding the next entry.
	 */
	private void openList(int level, StringBuilder text, int previousLevel) {
		if (level <= previousLevel) {
			// same or lower level as previous item, close previous and open new
			text.append("</li>\n<li>");
			return;
		}
		for (int i = previousLevel; i < level; i++) {
			// open lists to current level
			text.append("<ul>\n<li>");
		}
	}

	/**
	 * Force a TOC to appear, even if there are fewer than four headings.
	 *
	 * @param forceTOC Set to <code>true</code> if a TOC is being forced
	 *  to appear, false otherwise.
	 */
	public void setForceTOC(boolean forceTOC) {
		this.forceTOC = forceTOC;
	}

	/**
	 * Set the current table of contents status, such as "no table of contents
	 * allowed" or "uninitialized".
	 *
	 * @param status The current status of this table of contents object.
	 */
	public void setStatus(int status) {
		if (status == STATUS_TOC_INITIALIZED) {
			// keep track of how many TOC insertion tags are present
			this.insertTagCount++;
		}
		this.status = status;
	}

	/**
	 * Return the number of entries in this TOC object.
	 *
	 * @return The number of entries in this table of contents object.
	 */
	public int size() {
		return this.getEntries().size();
	}

	/**
	 * Return an HTML representation of this table of contents object.
	 *
	 * @param locale The locale of the user viewing the TOC, used for formatting
	 *  the header message key.
	 * @return An HTML representation of this table of contents object.
	 */
	private String toHTML(Locale locale) throws IOException {
		StringBuilder text = new StringBuilder();
		int adjustedLevel = 0;
		int previousLevel = 0;
		Object[] args = new Object[3];
		for (TableOfContentsEntry entry : this.getEntries().values()) {
			// adjusted level determines how far to indent the list
			adjustedLevel = ((entry.level - minLevel) + 1);
			// cannot increase TOC indent level more than one level at a time
			if (adjustedLevel > (previousLevel + 1)) {
				adjustedLevel = previousLevel + 1;
			}
			if (adjustedLevel <= Environment.getIntValue(Environment.PROP_PARSER_TOC_DEPTH)) {
				// only display if not nested deeper than max
				closeList(adjustedLevel, text, previousLevel);
				openList(adjustedLevel, text, previousLevel);
				// arguments are anchor name, TOC level display, and TOC entry text
				args[0] = entry.name;
				args[1] = this.nextTocPrefix(adjustedLevel - 1);
				args[2] = entry.text;
				text.append(WikiUtil.formatFromTemplate(TEMPLATE_TOC_ENTRY, args));
				previousLevel = adjustedLevel;
			}
		}
		closeList(0, text, previousLevel);
		args = new Object[4];
		// arguments are TOC header text and TOC entries
		args[0] = Utilities.formatMessage("topic.toc.header", locale);
		args[1] = Utilities.formatMessage("topic.toc.label.hide", locale);
		args[2] = Utilities.formatMessage("topic.toc.label.show", locale);
		args[3] = text.toString();
		return WikiUtil.formatFromTemplate(TEMPLATE_TOC_CONTAINER, args);
	}

	/**
	 * Inner class holds TOC entries until they can be processed for display.
	 */
	class TableOfContentsEntry {

		final int level;
		final String name;
		final String text;

		/**
		 *
		 */
		TableOfContentsEntry(String name, String text, int level) {
			this.name = name;
			this.text = text;
			this.level = level;
		}
	}
}
