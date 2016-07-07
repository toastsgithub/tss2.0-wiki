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
package org.jamwiki.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiException;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicType;
import org.jamwiki.model.TopicVersion;
import org.jamwiki.model.WikiUser;
import org.jamwiki.parser.WikiLink;

/**
 * This class parse a TiddlyWiki file and imports it to JamWiki
 *
 * @author Michael Greifeneder mikegr@gmx.net
 * @deprecated Tiddly wiki import support has been unmaintained since JAMWiki
 *  0.6.0 and will be removed in a future release unless a new maintainer is
 *  found.
 */
public class TiddlyWikiParser {

	private static final WikiLogger logger = WikiLogger.getLogger(TiddlyWikiParser.class.getName());

	private static final String DIV_START = "<div tiddler";
	private static final String DIV_END = "</div>";
	private static final String TIDLLER = "tiddler";
	//private static final String MODIFIER = "modifier";
	private static final String MODIFIED = "modified";
	//private static final String TAGS = "tags";
	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmm");
	private StringBuilder messages = new StringBuilder();
	private String virtualWiki;
	private WikiUser user;
	private String authorDisplay;

	/**
	 * Facade for WikiBase. Used for enable unit testing.
	 * @author Michael Greifeneder mikegr@gmx.net
	 */
	public interface WikiBaseFascade {
		public void writeTopic(Topic topic, TopicVersion topicVersion, LinkedHashMap categories, List<String> links, Object transactionObject) throws DataAccessException, WikiException;
	}

	/**
	 * Defaul WikiBaseFascade for production.
	 */
	private WikiBaseFascade wikiBase = new WikiBaseFascade() {
		public void writeTopic(Topic topic, TopicVersion topicVersion, LinkedHashMap categories, List<String> links, Object transactionObject) throws DataAccessException, WikiException {
			WikiBase.getDataHandler().writeTopic(topic, topicVersion, null, null);
		}
	};

	private TiddlyWiki2MediaWikiTranslator translator = new TiddlyWiki2MediaWikiTranslator();

	/**
	 * Main constructor
	 *
	 * @param virtualWiki virtualWiki
	 * @param user user who is currently logged in
	 * @param authorDisplay A display value for the importing user, typically the IP address.
	 */
	public TiddlyWikiParser(String virtualWiki, WikiUser user, String authorDisplay) {
		this.virtualWiki = virtualWiki;
		this.user = user;
		this.authorDisplay = authorDisplay;
	}

	/**
	 * Use this contructor for test cases
	 *
	 * @param virtualWiki Name of VirtualWiki
	 * @param user User who is logged in.
	 * @param authorDisplay A display value for the importing user, typically the IP address.
	 * @param wikiBase Overwrites default WikiBaseFascade
	 */
	public TiddlyWikiParser(String virtualWiki, WikiUser user, String authorDisplay, WikiBaseFascade wikiBase) {
		this(virtualWiki, user, authorDisplay);
		this.wikiBase = wikiBase;
	}

	/**
	 * Parses file and returns default topic.
	 *
	 * @param file TiddlyWiki file
	 * @return main topic for this TiddlyWiki
	 */
	public String parse(File file) throws DataAccessException, IOException, WikiException {
		Reader r = new FileReader(file);
		BufferedReader br = new BufferedReader(r);
		return parse(br);
	}

	/**
	 * Parses TiddlyWiki content and returns default topic.
	 *
	 * @param br TiddlyWiki file content
	 * @return main topic for this TiddlyWiki
	 */
	public String parse(BufferedReader br) throws DataAccessException, IOException, WikiException {
		String line = br.readLine();
		boolean inTiddler = false;
		int start = 0;
		int end = 0;
		StringBuilder content = new StringBuilder();
		while (line != null) {
			if (inTiddler) {
				end = line.indexOf(DIV_END);
				if (end != -1) {
					inTiddler = false;
					content.append(line.substring(0, end));
					processContent(content.toString());
					content.setLength(0);
					line = line.substring(end);
				} else {
					content.append(line);
					line = br.readLine();
				}
			} else {
				start = line.indexOf(DIV_START);
				if (start != -1 && (line.indexOf("<div tiddler=\"%0\"") == -1)) {
					inTiddler = true;
					logger.debug("Ignoring:\n" + line.substring(0, start));
					line = line.substring(start);
				} else {
					logger.debug("Div tiddler not found in: \n" + line);
					line = br.readLine();
				}
			}
		}
		return "DefaultTiddlers";
	}

	/**
	 *
	 */
	private void processContent(String content) throws DataAccessException, IOException, WikiException {
		logger.debug("Content: " + content);
		String name = findName(content, TIDLLER);
		if (name == null|| "%0".equals(user)) {
			return;
		}
		/* no need for user
		String user = findName(content, MODIFIER);
		if (user == null ) {
			messages.append("WARN: ")
			return;
		}
		*/
		Date lastMod = null;
		try {
			lastMod = formater.parse(findName(content, MODIFIED));
		} catch (Exception e) {
			messages.append("WARNING: corrupt line: ").append(content);
		}
		if (lastMod == null) {
			return;
		}
		/* ignoring tags
		String tags = findName(content, TAGS);
		if (tags == null) {
			return;
		}
		*/
		int idx = content.indexOf('>');
		if (idx == -1) {
			logger.warn("No closing of tag");
			messages.append("WARNING: corrupt line: ").append(content);
			return;
		}
		String wikicode = content.substring(idx +1);
		wikicode = translator.translate(wikicode);
		messages.append("Adding topic " + name + "\n");
		saveTopic(name, lastMod, wikicode);
		logger.debug("Code:" + wikicode);
	}

	/**
	 *
	 */
	private void saveTopic(String name, Date lastMod, String content) throws DataAccessException, WikiException {
		WikiLink wikiLink = new WikiLink(null, virtualWiki, name);
		Topic topic = new Topic(virtualWiki, wikiLink.getNamespace(), wikiLink.getArticle());
		topic.setTopicContent(content);
		int charactersChanged = StringUtils.length(content);
		TopicVersion topicVersion = new TopicVersion(user, authorDisplay, "imported", content, charactersChanged);
		topicVersion.setEditDate(new Timestamp(lastMod.getTime()));
		// manage mapping bitween MediaWiki and JAMWiki namespaces
		topic.setTopicType(TopicType.ARTICLE);
		// Store topic in database
		wikiBase.writeTopic(topic, topicVersion, null, null, null);
	}

	/**
	 *
	 */
	private String findName(String content, String name) {
		int startIdx = content.indexOf(name);
		if (startIdx == -1) {
			logger.warn("no tiddler name found");
			return null;
		}
		startIdx = content.indexOf('\"', startIdx);
		int endIdx = content.indexOf('\"', startIdx+1);
		String value = content.substring(startIdx+1, endIdx);
		logger.debug(name + ":" + value);
		return value;
	}

	/**
	 *
	 */
	public String getOutput() {
		return messages.toString();
	}
}
