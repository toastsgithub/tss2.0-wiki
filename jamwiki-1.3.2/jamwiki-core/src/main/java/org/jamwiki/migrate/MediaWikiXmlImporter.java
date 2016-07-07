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
package org.jamwiki.migrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.Environment;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiException;
import org.jamwiki.WikiMessage;
import org.jamwiki.model.Namespace;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicVersion;
import org.jamwiki.parser.WikiLink;
import org.jamwiki.utils.WikiLogger;
import org.jamwiki.utils.WikiUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Provide functionality for importing a Mediawiki XML file into JAMWiki.
 */
public class MediaWikiXmlImporter extends DefaultHandler implements TopicImporter {

	private static final WikiLogger logger = WikiLogger.getLogger(MediaWikiXmlImporter.class.getName());
	/** Maximum number of topic versions that can be stored before being flushed to the database. */
	private static final int MAX_TOPIC_VERSION_BUFFER = 50;
	private static final SAXParserFactory SAX_PARSER_FACTORY;

	/** This map holds the current tag's attribute names and values.  It is cleared after an end-element is called and thus fails for nested elements. */
	private Map<String, String> currentAttributeMap = new HashMap<String, String>();
	/** Map used when converting namespaces.  Created for performance reasons to avoid recompiling patterns. */
	private Map<String, Pattern> convertNamespaceMap = new HashMap<String, Pattern>();
	/** This buffer holds the content of the current element during parsing.  It will be flushed after an end-element tag is reached. */
	private StringBuilder currentElementBuffer = new StringBuilder();
	private Topic currentTopic = null;
	private TopicVersion currentTopicVersion = new TopicVersion();
	private Map<Date, Integer> currentTopicVersions = new TreeMap<Date, Integer>();
	private final Map<String, String> mediawikiNamespaceMap = new HashMap<String, String>();
	private Map<Topic, List<Integer>> parsedTopics = new HashMap<Topic, List<Integer>>();
	private int previousTopicContentLength = 0;
	/** For performance reasons add topic versions to the dabase in batches. */
	private List<TopicVersion> topicVersionBuffer = new ArrayList<TopicVersion>();
	private String virtualWiki;

	static {
		// For big file parsing
		System.setProperty("entityExpansionLimit", "1000000");
		SAX_PARSER_FACTORY = SAXParserFactory.newInstance();
	}

	/**
	 *
	 */
	public Map<Topic, List<Integer>> importFromFile(File file, String virtualWiki) throws MigrationException {
		this.virtualWiki = virtualWiki;
		this.importWikiXml(file);
		return this.parsedTopics;
	}

	/**
	 *
	 */
	private void importWikiXml(File file) throws MigrationException {
		FileInputStream fis = null;
		try {
			// at least in 1.5, the SaxParser has a bug where files with names like "%25s"
			// will be read as "%s", generating FileNotFound exceptions.  To work around this
			// issue use a FileInputStream rather than just SAXParser.parse(file, handler)
			fis = new FileInputStream(file);
			SAXParser saxParser = SAX_PARSER_FACTORY.newSAXParser();
			saxParser.parse(fis, this);
		} catch (ParserConfigurationException e) {
			throw new MigrationException(e);
		} catch (IOException e) {
			throw new MigrationException(e);
		} catch (SAXException e) {
			if (e.getCause() instanceof DataAccessException || e.getCause() instanceof WikiException) {
				throw new MigrationException(e.getCause());
			} else {
				throw new MigrationException(e);
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Convert the Wikipedia article namespace (if any) to a JAMWiki article namespace.
	 */
	private String convertArticleNameFromWikipediaToJAMWiki(String fullName) {
		String ret = fullName;
		int pos = fullName.indexOf(Namespace.SEPARATOR);
		if (pos > 0) {
			String namespace = fullName.substring(0, pos);
			String title = fullName.substring(pos+1);
			String jamwikiNamespace = mediawikiNamespaceMap.get(namespace);
			if (!StringUtils.isBlank(jamwikiNamespace)) {
				// matching JAMWiki namespace found
				ret = jamwikiNamespace + Namespace.SEPARATOR + title;
			}
		}
		// remove any characters that are valid for Mediawiki but not JAMWiki
		ret = StringUtils.remove(ret, '?');
		return ret;
	}

	/**
	 * Convert all namespaces names from MediaWiki to JAMWiki local representation.
	 */
	private String convertToJAMWikiNamespaces(String topicContent) {
		// convert all namespaces names from MediaWiki to JAMWiki local representation
		Pattern pattern;
		for (Map.Entry<String, String> entry : mediawikiNamespaceMap.entrySet()) {
			if (entry.getValue() == null || StringUtils.equalsIgnoreCase(entry.getValue(), entry.getKey())) {
				continue;
			}
			pattern = this.convertNamespaceMap.get(entry.getKey());
			if (pattern == null) {
				// convert from Mediawiki to JAMWiki namespaces.  handle "[[", "[[:", "{{", "{{:".
				// note that "?:" is a regex non-capturing group.
				String patternString = "((?:(?:\\[\\[)|(?:\\{\\{))[ ]*(?::)?)" + entry.getKey() + Namespace.SEPARATOR;
				Pattern mediawikiPattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				pattern = mediawikiPattern;
				this.convertNamespaceMap.put(entry.getKey(), mediawikiPattern);
			}
			topicContent = pattern.matcher(topicContent).replaceAll("$1" + entry.getValue() + Namespace.SEPARATOR);
		}
		return topicContent;
	}

	/**
	 *
	 */
	private Timestamp parseMediaWikiTimestamp(String timestamp) {
		try {
			Date date = DateUtils.parseDate(timestamp, new String[]{MediaWikiConstants.ISO_8601_DATE_FORMAT});
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			// FIXME - this should be handled somehow
			return new Timestamp(System.currentTimeMillis());
		}
	}

	/**
	 * Initialize the current topic, validating that it does not yet exist.
	 */
	private void initCurrentTopic(String topicName) throws SAXException {
		topicName = convertArticleNameFromWikipediaToJAMWiki(topicName);
		WikiLink wikiLink = new WikiLink(null, this.virtualWiki, topicName);
		Topic existingTopic = null;
		try {
			existingTopic = WikiBase.getDataHandler().lookupTopic(this.virtualWiki, topicName, false);
		} catch (DataAccessException e) {
			throw new SAXException("Failure while validating topic name: " + this.virtualWiki + ':' + topicName, e);
		}
		if (existingTopic != null && existingTopic.getVirtualWiki().equals(this.virtualWiki)) {
			// do a second comparison of capitalized topic names in a case-sensitive way
			// since the initial topic lookup will return a case-insensitive match for some
			// namespaces.
			String existingTopicName = (Environment.getBooleanValue(Environment.PROP_PARSER_ALLOW_CAPITALIZATION)) ? StringUtils.capitalize(existingTopic.getPageName()) : existingTopic.getPageName();
			String importTopicName = (Environment.getBooleanValue(Environment.PROP_PARSER_ALLOW_CAPITALIZATION)) ? StringUtils.capitalize(wikiLink.getArticle()) : wikiLink.getArticle();
			if (StringUtils.equals(existingTopicName, importTopicName)) {
				// FIXME - update so that this merges any new versions instead of throwing an error
				WikiException e = new WikiException(new WikiMessage("import.error.topicexists", topicName));
				throw new SAXException("Topic " + this.virtualWiki + ':' + topicName + " already exists and cannot be imported", e);
			}
		}
		this.currentTopic = new Topic(this.virtualWiki, wikiLink.getNamespace(), wikiLink.getArticle());
		this.currentTopic.setTopicType(WikiUtil.findTopicTypeForNamespace(wikiLink.getNamespace()));
	}

	/**
	 * Write a topic version record to the database.
	 */
	private void commitTopicVersion() throws SAXException {
		// FIXME - support rollback
		this.currentTopic.setTopicContent(currentTopicVersion.getVersionContent());
		// only the final import version is logged
		this.currentTopicVersion.setLoggable(false);
		// no recent change record needed - can be added by reloading all recent changes if desired
		this.currentTopicVersion.setRecentChangeAllowed(false);
		this.topicVersionBuffer.add(this.currentTopicVersion);
		this.writeTopicVersion(false);
	}

	/**
	 * After all topic versions have been created for a topic, go back and set the previous topic version
	 * ID values for each version.  This must be done after parsing since the XML file may not contain
	 * version records sorted chronologically from oldest to newest.
	 */
	private void orderTopicVersions() throws SAXException {
		if (this.currentTopicVersions.isEmpty()) {
			throw new SAXException("No topic versions found for " + this.currentTopic.getName());
		}
		// topic versions are stored in a tree map to allow sorting... convert to a list
		List<Integer> currentTopicVersionIdList = new ArrayList<Integer>(this.currentTopicVersions.values());
		try {
			WikiBase.getDataHandler().orderTopicVersions(this.currentTopic, currentTopicVersionIdList);
		} catch (DataAccessException e) {
			throw new SAXException("Failure while ordering topic versions for topic: " + this.currentTopic.getName(), e);
		}
		this.parsedTopics.put(this.currentTopic, currentTopicVersionIdList);
	}

	//===========================================================
	// SAX DocumentHandler methods
	//===========================================================

	/**
	 * start of xml-tag
	 *
	 * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
	 *  if Namespace processing is not being performed.
	 * @param localName The local name (without prefix), or the empty string if Namespace processing
	 *  is not being performed.
	 * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @param attrs The attributes attached to the element. If there are no attributes, it shall be an
	 *  empty Attributes object.
	 */
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		this.currentElementBuffer = new StringBuilder();
		this.currentAttributeMap = new HashMap<String, String>();
		String key;
		if (attrs != null) {
			// populate the attribute map
			for (int i = 0; i < attrs.getLength(); i++) {
				key = attrs.getQName(i);
				if (!StringUtils.isBlank(key)) {
					this.currentAttributeMap.put(key, attrs.getValue(i));
				}
			}
		}
		if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION.equals(qName)) {
			this.currentTopicVersion = new TopicVersion();
			this.currentTopicVersion.setEditType(TopicVersion.EDIT_IMPORT);
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC.equals(qName)) {
			this.currentTopicVersions = new TreeMap<Date, Integer>();
		}
	}

	/**
	 * end of xml-tag
	 *
	 * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
	 *  if Namespace processing is not being performed.
	 * @param localName The local name (without prefix), or the empty string if Namespace processing
	 *  is not being performed.
	 * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (StringUtils.equals(MediaWikiConstants.MEDIAWIKI_ELEMENT_NAMESPACE, qName)) {
			int key = NumberUtils.toInt(this.currentAttributeMap.get("key"));
			try {
				Namespace jamwikiNamespace = WikiBase.getDataHandler().lookupNamespaceById(key);
				if (jamwikiNamespace != null) {
					String mediawikiNamespace = currentElementBuffer.toString().trim();
					mediawikiNamespaceMap.put(mediawikiNamespace, jamwikiNamespace.getLabel(this.virtualWiki));
				}
			} catch (DataAccessException e) {
				throw new SAXException("Failure while processing namespace with ID: " + key, e);
			}
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_NAME.equals(qName)) {
			String topicName = currentElementBuffer.toString().trim();
			this.initCurrentTopic(topicName);
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_CONTENT.equals(qName)) {
			String topicContent = this.convertToJAMWikiNamespaces(currentElementBuffer.toString().trim());
			currentTopicVersion.setVersionContent(topicContent);
			currentTopicVersion.setCharactersChanged(StringUtils.length(topicContent) - previousTopicContentLength);
			previousTopicContentLength = StringUtils.length(topicContent);
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION_COMMENT.equals(qName)) {
			this.currentTopicVersion.setEditComment(currentElementBuffer.toString().trim());
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION_MINOR_EDIT.equals(qName)) {
			this.currentTopicVersion.setEditType(TopicVersion.EDIT_MINOR);
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION_EDIT_DATE.equals(qName)) {
			this.currentTopicVersion.setEditDate(this.parseMediaWikiTimestamp(currentElementBuffer.toString().trim()));
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION_IP.equals(qName) || MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION_USERNAME.equals(qName)) {
			// Login name in Mediawiki can be longer than 100 characters, so trim to conform to
			// JAMWiki limits.  In general very long login names seem to be used only by vandals,
			// so this should be an acceptable workaround.
			String authorDisplay = currentElementBuffer.toString().trim();
			if (authorDisplay.length() > 100) {
				authorDisplay = authorDisplay.substring(0, 100);
			}
			this.currentTopicVersion.setAuthorDisplay(authorDisplay);
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC_VERSION.equals(qName)) {
			this.commitTopicVersion();
		} else if (MediaWikiConstants.MEDIAWIKI_ELEMENT_TOPIC.equals(qName)) {
			// flush any pending topic version data
			this.writeTopicVersion(true);
			this.orderTopicVersions();
		}
	}

	/**
	 * When the parser encounters plain text (not XML elements), it calls this method
	 * which accumulates them in a string buffer
	 */
	public void characters(char buf[], int offset, int len) throws SAXException {
		currentElementBuffer.append(buf, offset, len);
	}

	/**
	 *
	 */
	private void writeTopicVersion(boolean forceWrite) throws SAXException {
		try {
			// for performance reasons write the topic once to create an initial record, then write
			// only the version record.
			if (this.currentTopic.getTopicId() <= 0) {
				// metadata is needed only for the final import version, so for performance reasons
				// do not include category or link data for older versions
				WikiBase.getDataHandler().writeTopic(this.currentTopic, null, null, null);
			} else if (forceWrite || this.topicVersionBuffer.size() >= MAX_TOPIC_VERSION_BUFFER) {
				WikiBase.getDataHandler().writeTopicVersions(this.currentTopic, this.topicVersionBuffer);
				for (TopicVersion topicVersion : this.topicVersionBuffer) {
					this.currentTopicVersions.put(topicVersion.getEditDate(), topicVersion.getTopicVersionId());
				}
				this.topicVersionBuffer = new ArrayList<TopicVersion>();
			}
		} catch (DataAccessException e) {
			throw new SAXException("Failure while writing topic: " + this.currentTopic.getName(), e);
		} catch (WikiException e) {
			throw new SAXException("Failure while writing topic: " + this.currentTopic.getName(), e);
		}
	}
}
