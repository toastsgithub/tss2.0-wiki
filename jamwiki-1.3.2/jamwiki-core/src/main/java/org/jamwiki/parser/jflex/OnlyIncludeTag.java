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
package org.jamwiki.parser.jflex;

import org.jamwiki.utils.WikiLogger;

/**
 * This class parses nowiki tags of the form <code>&lt;onlyinclude&gt;content&lt;/onlyinclude&gt;</code>.
 */
public class OnlyIncludeTag implements JFlexParserTag {

	private static final WikiLogger logger = WikiLogger.getLogger(OnlyIncludeTag.class.getName());

	/**
	 * Parse a call to a Mediawiki onlyinclude tag of the form
	 * "<onlyinclude>text</onlyinclude>" and return the resulting output.
	 */
	public String parse(JFlexLexer lexer, String raw, Object... args) {
		if (lexer.getMode() <= JFlexParser.MODE_MINIMAL) {
			return raw;
		}
		if (lexer.getParserInput().getTemplateDepth() == 0) {
			// onlyinclude only generates results during transclusion, otherwise the
			// content is ignored.
			return "";
		}
		// there is no need to parse the tag content since that will be done by TemplateTag
		String content = JFlexParserUtil.tagContent(raw);
		// HACK - put the onlyinclude content in a temp param to be used by the
		// TemplateTag.parseTemplateBody method.  this is necessary because onlyinclude
		// supersedes anything that might have been parsed before or after the onlyinclude
		// tag.
		lexer.getParserInput().addTempParam(TemplateTag.TEMPLATE_ONLYINCLUDE, content);
		return content;
	}
}
