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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.Environment;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiException;
import org.jamwiki.model.Namespace;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicType;
import org.jamwiki.parser.ExcessiveNestingException;
import org.jamwiki.parser.LinkUtil;
import org.jamwiki.parser.ParserException;
import org.jamwiki.parser.ParserInput;
import org.jamwiki.parser.ParserOutput;
import org.jamwiki.parser.WikiLink;
import org.jamwiki.utils.Utilities;
import org.jamwiki.utils.WikiLogger;

/**
 * <code>TemplateTag</code> parses Mediawiki template syntax, which allows
 * programmatic structures to be embedded in wiki syntax.
 */
public class TemplateTag implements JFlexParserTag {

	private static final WikiLogger logger = WikiLogger.getLogger(TemplateTag.class.getName());
	protected static final String TEMPLATE_INCLUSION = "template-inclusion";
	protected static final String TEMPLATE_ONLYINCLUDE = "template-onlyinclude";

	/**
	 * Once the template call has been parsed and the template values have been
	 * determined, parse the template body and apply those template values.
	 * Parameters may be embedded or have default values, so there is some
	 * voodoo magic that happens here to first parse any embedded values, and
	 * to apply default values when no template value has been set.
	 */
	private String applyParameter(ParserInput parserInput, ParserOutput parserOutput, String param, Map<String, String> parameterValues) throws ParserException {
		String content = param.substring("{{{".length(), param.length() - "}}}".length());
		// re-parse in case of embedded templates or params
		content = this.parseTemplateBody(parserInput, parserOutput, content, parameterValues);
		String name = this.parseParamName(content);
		String value = parameterValues.get(name);
		if (value != null) {
			return value;
		}
		String defaultValue = this.parseParamDefaultValue(parserInput, parserOutput, content);
		return (defaultValue == null) ? " " : defaultValue;
	}

	/**
	 * Parse a call to a Mediawiki template of the form "{{template|param1|param2}}"
	 * and return the resulting template output.
	 */
	public String parse(JFlexLexer lexer, String raw, Object... args) throws ParserException {
		// validate and extract the template content
		if (StringUtils.isBlank(raw) || !raw.startsWith("{{") || !raw.endsWith("}}")) {
			throw new ParserException("Empty or invalid template text: " + raw);
		}
		String templateContent = raw.substring("{{".length(), raw.length() - "}}".length()).trim();
		boolean isSubstitution = (templateContent.startsWith("subst:") && templateContent.length() > "subst:".length());
		if (!isSubstitution && lexer.getMode() < JFlexParser.MODE_TEMPLATE) {
			return raw;
		}
		try {
			if (isSubstitution && lexer.getMode() >= JFlexParser.MODE_MINIMAL) {
				return this.parseSubstitution(lexer.getParserInput(), lexer.getParserOutput(), raw, templateContent);
			}
			return this.parseTemplateOutput(lexer.getParserInput(), lexer.getParserOutput(), lexer.getMode(), raw, true);
		} catch (ExcessiveNestingException e) {
			logger.warn("Excessive template nesting in topic " + lexer.getParserInput().getVirtualWiki() + ':' + lexer.getParserInput().getTopicName());
			// convert to a link so that the user can fix the template
			WikiLink wikiLink = this.parseTemplateName(lexer.getParserInput(), lexer.getParserOutput(), templateContent);
			String templateName = wikiLink.getDestination();
			if (!wikiLink.getColon() && !wikiLink.getNamespace().equals(Namespace.namespace(Namespace.TEMPLATE_ID))) {
				templateName = Namespace.namespace(Namespace.TEMPLATE_ID).getLabel(lexer.getParserInput().getVirtualWiki()) + Namespace.SEPARATOR + StringUtils.capitalize(templateName);
			}
			return "[[" + templateName + "]]";
		} catch (DataAccessException e) {
			throw new ParserException("Data access exception while parsing: " + raw, e);
		}
	}

	/**
	 * Parses the template content and returns the parsed output.  If there is no result (such
	 * as when a template does not exist) this method will either return an edit link to the
	 * template topic page, or if allowTemplateEdit is <code>false</code> it will return
	 * <code>null</code> (used with substitutions, where an edit link should not be shown).
	 */
	private String parseTemplateOutput(ParserInput parserInput, ParserOutput parserOutput, int mode, String raw, boolean allowTemplateEdit) throws DataAccessException, ParserException {
		String templateContent = raw.substring("{{".length(), raw.length() - "}}".length());
		parserInput.incrementTemplateDepth();
		if (parserInput.getTemplateDepth() > Environment.getIntValue(Environment.PROP_PARSER_MAX_TEMPLATE_DEPTH)) {
			parserInput.decrementTemplateDepth();
			throw new ExcessiveNestingException("Potentially infinite parsing loop - over " + parserInput.getTemplateDepth() + " template inclusions while parsing topic " + parserInput.getVirtualWiki() + ':' + parserInput.getTopicName());
		}
		// check for magic word or parser function
		String result = this.processParserFunctionAndMagicWord(parserInput, parserOutput, mode, templateContent, raw);
		if (result != null) {
			parserInput.decrementTemplateDepth();
			return result;
		}
		// update the raw value to handle cases such as a signature in the template content
		raw = "{{" + templateContent + "}}";
		// extract the template name
		WikiLink wikiLink = this.parseTemplateName(parserInput, parserOutput, templateContent);
		String name = wikiLink.getDestination();
		// parse in case of something like "{{PAGENAME}}/template"
		name = this.processNestedTemplates(parserInput, parserOutput, name);
		String templateName = name;
		try {
			// do not process the template if it's an invalid topic name
			LinkUtil.validateTopicName(parserInput.getVirtualWiki(), templateName, false);
		} catch (WikiException e) {
			return raw;
		}
		// now see if a template with that name exists or if this is an inclusion
		Topic templateTopic = null;
		if (!wikiLink.getColon()) {
			if (!wikiLink.getNamespace().equals(Namespace.namespace(Namespace.TEMPLATE_ID))) {
				templateName = Namespace.namespace(Namespace.TEMPLATE_ID).getLabel(parserInput.getVirtualWiki()) + Namespace.SEPARATOR + StringUtils.capitalize(name);
			}
			templateTopic = WikiBase.getDataHandler().lookupTopic(parserInput.getVirtualWiki(), templateName, false);
		}
		boolean inclusion = wikiLink.getColon();
		if (templateTopic != null) {
			name = templateName;
		} else {
			// otherwise see if it's an inclusion
			templateTopic = WikiBase.getDataHandler().lookupTopic(parserInput.getVirtualWiki(), name, false);
			name = ((templateTopic == null && !wikiLink.getColon()) ? templateName : name);
			inclusion = (templateTopic != null || wikiLink.getColon());
		}
		// get the parsed template body
		this.processTemplateMetadata(parserOutput, templateTopic, name);
		if (mode <= JFlexParser.MODE_MINIMAL) {
			result = raw;
		} else {
			// make sure template was not redirected
			if (templateTopic != null && templateTopic.getTopicType() == TopicType.REDIRECT) {
				templateTopic = LinkUtil.findRedirectedTopic(templateTopic, 0);
				name = templateTopic.getName();
			}
			if (templateTopic != null && templateTopic.getTopicType() == TopicType.REDIRECT) {
				// redirection target does not exist
				templateTopic = null;
			}
			if (inclusion) {
				result = this.processTemplateInclusion(parserInput, parserOutput, templateTopic, templateContent, name);
			} else if (templateTopic == null) {
				result = ((allowTemplateEdit) ? "[[" + name + "]]" : null);
			} else {
				result = this.processTemplateContent(parserInput, parserOutput, templateTopic, templateContent);
			}
		}
		parserInput.decrementTemplateDepth();
		return result;
	}

	/**
	 * Given template parameter content of the form "name" or "name|default",
	 * return the default value if it exists.
	 */
	private String parseParamDefaultValue(ParserInput parserInput, ParserOutput parserOutput, String raw) throws ParserException {
		List<String> tokens = JFlexParserUtil.tokenizeParamString(raw);
		if (tokens.size() < 2) {
			return null;
		}
		// table elements mess up default processing, so just return anything after
		// the first parameter to avoid having to implement special table logic
		String param1 = tokens.get(0);
		String value = raw.substring(param1.length() + 1);
		return this.processNestedTemplates(parserInput, parserOutput, value);
	}

	/**
	 * Given template parameter content of the form "name" or "name|default",
	 * return the parameter name.
	 */
	private String parseParamName(String raw) throws ParserException {
		int pos = raw.indexOf('|');
		String name = ((pos != -1) ? raw.substring(0, pos) : raw).trim();
		if (StringUtils.isBlank(name)) {
			// FIXME - no need for an exception
			throw new ParserException("No parameter name specified");
		}
		return name;
	}

	/**
	 * Determine if template content is of the form "subst:XXX".  If it is,
	 * process it, otherwise return <code>null</code>.
	 */
	private String parseSubstitution(ParserInput parserInput, ParserOutput parserOutput, String raw, String templateContent) throws DataAccessException, ParserException {
		// get the substitution content
		String substContent = templateContent.trim().substring("subst:".length()).trim();
		if (substContent.length() == 0) {
			return null;
		}
		// re-parse the substitution value.  make sure it is parsed in at least MODE_TEMPLATE
		// so that values are properly replaced prior to saving.
		String output = this.parseTemplateOutput(parserInput, parserOutput, JFlexParser.MODE_TEMPLATE, "{{" + substContent + "}}", false);
		return (output == null) ? raw : output;
	}

	/**
	 * After template parameter values have been set, process the template body
	 * and replace parameters with parameter values or defaults, processing any
	 * embedded parameters or templates.
	 */
	private String parseTemplateBody(ParserInput parserInput, ParserOutput parserOutput, String content, Map<String, String> parameterValues) throws ParserException {
		StringBuilder output = new StringBuilder();
		char current, case4Char;
		String substring, param;
		int endPos, case1EndPos, case3EndPos;
		// find template parameters of the form {{{0}}}
		for (int pos = 0; pos < content.length(); pos++) {
			current = content.charAt(pos);
			substring = content.substring(pos);
			if (!substring.startsWith("{{{")) {
				// not a template parameter, move to the next character
				output.append(current);
				continue;
			}
			// this may be a template parameter, but check for various sub-patterns to be sure
			endPos = Utilities.findMatchingEndTag(content, pos, "{{{", "}}}");
			if (endPos == -1) {
				// no matching end tag
				output.append(current);
				continue;
			}
			endPos += "}}}".length();
			// there are several sub-patterns that need to be analyzed:
			// 1. {{{1|{{PAGENAME}}}}}
			// 2. {{{{{1}}}}}
			// 3. {{{template}} x {{template}}}
			// 4. {{{1|{{{2}}}}}}
			case1EndPos = Utilities.findMatchingEndTag(content, pos, "{", "}") + "}".length();
			if (endPos < case1EndPos && content.substring(case1EndPos - 3, case1EndPos).equals("}}}")) {
				// case #1
				endPos = case1EndPos;
			}
			if (substring.startsWith("{{{{{") && content.substring(endPos - 5, endPos).equals("}}}}}")) {
				// case #2 (note: endPos updated in the previous step)
				output.append("{{");
				pos++;
				continue;
			}
			case3EndPos = Utilities.findMatchingEndTag(content, pos + 1, "{{", "}}") + "}}".length();
			if (case3EndPos != (endPos - 1)) {
				// either case #3 or case #4
				case4Char = content.charAt(case3EndPos + 1);
				if (case4Char != '}') {
					// case #3
					output.append(current);
					continue;
				}
			}
			param = content.substring(pos, endPos);
			output.append(this.applyParameter(parserInput, parserOutput, param, parameterValues));
			pos = endPos - 1;
		}
		return JFlexParserUtil.parseFragment(parserInput, parserOutput, output.toString().trim(), JFlexParser.MODE_TEMPLATE);
	}

	/**
	 * Given a template call of the form "template|param|param", return
	 * the template name.
	 */
	private WikiLink parseTemplateName(ParserInput parserInput, ParserOutput parserOutput, String raw) throws ParserException {
		// parse to handle cases such as "Example{{padleft:3|2|0}}"
		String name = this.processNestedTemplates(parserInput, parserOutput, raw);
		int pos = name.indexOf('|');
		if (pos != -1) {
			name = name.substring(0, pos);
		}
		name = Utilities.decodeTopicName(name.trim(), true);
		if (StringUtils.isBlank(name)) {
			// FIXME - no need for an exception
			throw new ParserException("No template name specified");
		}
		boolean inclusion = false;
		if (name.startsWith(Namespace.SEPARATOR)) {
			if (name.length() == 1) {
				// FIXME - no need for an exception
				throw new ParserException("No template name specified");
			}
			inclusion = true;
			name = name.substring(1).trim();
		}
		WikiLink wikiLink = LinkUtil.parseWikiLink(parserInput.getContext(), parserInput.getVirtualWiki(), name);
		wikiLink.setColon(inclusion);
		return wikiLink;
	}

	/**
	 * Given a template call of the form "{{name|param=value|param=value}}"
	 * parse the parameter names and values.
	 */
	private Map<String, String> parseTemplateParameterValues(String templateContent) throws ParserException {
		Map<String, String> parameterValues = new HashMap<String, String>();
		List<String> tokens = JFlexParserUtil.tokenizeParamString(templateContent);
		if (tokens.isEmpty()) {
			throw new ParserException("No template name found in " + templateContent);
		}
		int count = -1;
		for (String token : tokens) {
			count++;
			if (count == 0) {
				// first token is template name
				continue;
			}
			String[] nameValue = this.tokenizeNameValue(token);
			String value = (nameValue[1] == null) ? null : nameValue[1].trim();
			// the user can specify params of the form "2=first|1=second", so check to make
			// sure an index value hasn't already been used.
			if (!parameterValues.containsKey(Integer.toString(count))) {
				parameterValues.put(Integer.toString(count), value);
			}
			// if there is a named parameter store it as well as a count-based parameter, just in
			// case the template specifies both
			if (!StringUtils.isBlank(nameValue[0])) {
				parameterValues.put(nameValue[0].trim(), value);
			}
		}
		return parameterValues;
	}

	/**
	 * Parsing is expensive, so when testing for nested templates first do a
	 * sanity check to see if there is template syntax in the string being
	 * parsed.
	 */
	private String processNestedTemplates(ParserInput parserInput, ParserOutput parserOutput, String content) throws ParserException {
		int pos = content.indexOf("{{");
		if (pos == -1) {
			return content;
		}
		pos = content.indexOf("}}", pos);
		if (pos == -1) {
			return content;
		}
		return JFlexParserUtil.parseFragment(parserInput, parserOutput, content, JFlexParser.MODE_TEMPLATE);
	}

	/**
	 * Determine if the parser content represents a magic word or parser
	 * function, and if so return the appropriate parsed content.  Otherwise
	 * return <code>null</code>.
	 */
	private String processParserFunctionAndMagicWord(ParserInput parserInput, ParserOutput parserOutput, int mode, String templateContent, String raw) throws DataAccessException, ParserException {
		String[] magicWordInfo = MagicWordUtil.parseMagicWordInfo(templateContent);
		if (magicWordInfo != null) {
			if (mode <= JFlexParser.MODE_MINIMAL) {
				return raw;
			} else {
				return MagicWordUtil.processMagicWord(parserInput, parserOutput, mode, magicWordInfo[0], magicWordInfo[1]);
			}
		}
		String[] parserFunctionInfo = ParserFunctionUtil.parseParserFunctionInfo(templateContent);
		if (parserFunctionInfo != null) {
			if (mode <= JFlexParser.MODE_MINIMAL) {
				return raw;
			} else {
				return ParserFunctionUtil.processParserFunction(parserInput, parserOutput, mode, parserFunctionInfo[0], parserFunctionInfo[1]);
			}
		}
		return null;
	}

	/**
	 * Given a template call of the form "{{name|param|param}}" return the
	 * parsed output.
	 */
	private String processTemplateContent(ParserInput parserInput, ParserOutput parserOutput, Topic templateTopic, String templateContent) throws ParserException {
		// set template parameter values
		Map<String, String> parameterValues = this.parseTemplateParameterValues(templateContent);
		// parse the template content for noinclude, onlyinclude and includeonly tags
		String templateBody = JFlexParserUtil.parseFragment(parserInput, parserOutput, templateTopic.getTopicContent().trim(), JFlexParser.MODE_TEMPLATE_BODY);
		if (parserInput.getTempParam(TEMPLATE_ONLYINCLUDE) != null) {
			// HACK! If an onlyinclude tag is encountered in the previous fragment parse
			// then that tag's parsed output is stored in the TEMPLATE_ONLYINCLUDE param.
			// This hack is necessary because onlyinclude indicates that ONLY the
			// onlyinclude content is relevant, and anything parsed before or after that
			// tag must be ignored.
			templateBody = (String)parserInput.getTempParam(TEMPLATE_ONLYINCLUDE);
			parserInput.removeTempParam(TEMPLATE_ONLYINCLUDE);
		}
		return this.parseTemplateBody(parserInput, parserOutput, templateBody, parameterValues);
	}

	/**
	 * Given a template call of the form "{{:name}}" parse the template
	 * inclusion.
	 */
	private String processTemplateInclusion(ParserInput parserInput, ParserOutput parserOutput, Topic templateTopic, String templateContent, String name) throws ParserException {
		if (templateTopic == null) {
			return "[[" + name + "]]";
		}
		// FIXME - disable section editing
		int inclusion = (parserInput.getTempParam(TEMPLATE_INCLUSION) == null) ? 1 : (Integer)parserInput.getTempParam(TEMPLATE_INCLUSION) + 1;
		if (inclusion > Environment.getIntValue(Environment.PROP_PARSER_MAX_INCLUSIONS)) {
			throw new ExcessiveNestingException("Potentially infinite inclusions - over " + inclusion + " template inclusions while parsing topic " + parserInput.getVirtualWiki() + ':' + parserInput.getTopicName());
		}
		parserInput.addTempParam(TEMPLATE_INCLUSION, inclusion);
		return this.processTemplateContent(parserInput, parserOutput, templateTopic, templateContent);
	}

	/**
	 * Process template values, setting link and other metadata output values.
	 */
	private void processTemplateMetadata(ParserOutput parserOutput, Topic templateTopic, String name) {
		name = (templateTopic != null) ? templateTopic.getName() : name;
		parserOutput.addLink(name);
		parserOutput.addTemplate(name);
	}

	/**
	 * Determine if params are of the form name=value, and if so split
	 * them into an array pairing.
	 */
	private String[] tokenizeNameValue(String content) {
		String[] results = new String[2];
		results[0] = null;
		results[1] = content;
		int pos = content.indexOf('=');
		if (pos > 0) {
			String param = content.substring(0, pos);
			if (!StringUtils.isBlank(param)) {
				results[0] = param.trim();
				// set to null unless there is content after the equals sign
				results[1] = (pos < (content.length() - 1)) ? content.substring(pos + 1) : null;
			}
		}
		return results;
	}
}
