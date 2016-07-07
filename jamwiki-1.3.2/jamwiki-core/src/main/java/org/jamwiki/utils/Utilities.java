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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides a variety of basic utility methods that are not
 * dependent on any other classes within the org.jamwiki package structure.
 */
public abstract class Utilities {

	private static final WikiLogger logger = WikiLogger.getLogger(Utilities.class.getName());

	private static final String ipv4Pattern = "(?:(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "(?:[0-9a-f]{1,4}:){7}(?:[0-9a-f]){1,4}";
	private static final Pattern VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
	private static final Pattern VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);

	/**
	 * Convert a string value from one encoding to another.
	 *
	 * @param text The string that is to be converted.
	 * @param fromEncoding The encoding that the string is currently encoded in.
	 * @param toEncoding The encoding that the string is to be encoded to.
	 * @return The encoded string.
	 */
	public static String convertEncoding(String text, String fromEncoding, String toEncoding) {
		if (StringUtils.isBlank(text)) {
			return text;
		}
		if (StringUtils.isBlank(fromEncoding)) {
			logger.warn("No character encoding specified to convert from, using UTF-8");
			fromEncoding = "UTF-8";
		}
		if (StringUtils.isBlank(toEncoding)) {
			logger.warn("No character encoding specified to convert to, using UTF-8");
			toEncoding = "UTF-8";
		}
		try {
			text = new String(text.getBytes(fromEncoding), toEncoding);
		} catch (UnsupportedEncodingException e) {
			// bad encoding
			logger.warn("Unable to convert value " + text + " from " + fromEncoding + " to " + toEncoding, e);
		}
		return text;
	}

	/**
	 * Decode a value that has been retrieved from a servlet request.  This
	 * method will replace any underscores with spaces.
	 *
	 * @param url The encoded value that is to be decoded.
	 * @param decodeUnderlines Set to <code>true</code> if underlines should
	 *  be automatically converted to spaces.
	 * @return A decoded value.
	 */
	public static String decodeTopicName(String url, boolean decodeUnderlines) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		return (decodeUnderlines) ? StringUtils.replace(url, "_", " ") : url;
	}

	/**
	 * Decode a value that has been retrieved directly from a URL or file
	 * name.  This method will URL decode the value and then replace any
	 * underscores with spaces.  Note that this method SHOULD NOT be called
	 * for values retrieved using request.getParameter(), but only values
	 * taken directly from a URL.
	 *
	 * @param url The encoded value that is to be decoded.
	 * @param decodeUnderlines Set to <code>true</code> if underlines should
	 *  be automatically converted to spaces.
	 * @return A decoded value.
	 */
	public static String decodeAndEscapeTopicName(String url, boolean decodeUnderlines) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		String result = url;
		try {
			result = URLDecoder.decode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// this should never happen
			throw new IllegalStateException("Unsupporting encoding UTF-8");
		}
		return Utilities.decodeTopicName(result, decodeUnderlines);
	}

	/**
	 * Convert a delimited string to a list.
	 *
	 * @param delimitedString A string consisting of the delimited list items.
	 * @param delimiter The string used as the delimiter.
	 * @return A list consisting of the delimited string items, or <code>null</code> if the
	 *  string is <code>null</code> or empty.
	 */
	public static List<String> delimitedStringToList(String delimitedString, String delimiter) {
		if (delimiter == null) {
			throw new IllegalArgumentException("Attempt to call Utilities.delimitedStringToList with no delimiter specified");
		}
		if (StringUtils.isBlank(delimitedString)) {
			return null;
		}
		return Arrays.asList(StringUtils.splitByWholeSeparator(delimitedString, delimiter));
	}

	/**
	 * Encode a value for use a topic name.  This method will replace any
	 * spaces with underscores.
	 *
	 * @param url The decoded value that is to be encoded.
	 * @return An encoded value.
	 */
	public static String encodeTopicName(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		return StringUtils.replace(url, " ", "_");
	}

	/**
	 * Encode a topic name for use in a URL.  This method will replace spaces
	 * with underscores and URL encode the value, but it will not URL encode
	 * colons.
	 *
	 * @param url The topic name to be encoded for use in a URL.
	 * @return The encoded topic name value.
	 */
	public static String encodeAndEscapeTopicName(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		String result = Utilities.encodeTopicName(url);
		try {
			result = URLEncoder.encode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// this should never happen
			throw new IllegalStateException("Unsupporting encoding UTF-8");
		}
		// un-encode colons
		result = StringUtils.replace(result, "%3A", ":");
		// un-encode forward slashes
		result = StringUtils.replace(result, "%2F", "/");
		return result;
	}

	/**
	 * Search through content, starting at a specific position, and search for the
	 * first position of a matching end tag for a specified start tag.  For instance,
	 * if called with a start tag of "<b>" and an end tag of "</b>", this method
	 * will operate as follows:
	 *
	 * "01<b>567</b>23" returns 8.
	 * "01<b>56<b>01</b>67</b>23" returns 18.
	 *
	 * @param content The string to be searched.
	 * @param start The position within the string to start searching from (inclusive).
	 *  Only characters after this position in the string will be examined.
	 * @param startToken The opening tag to match.
	 * @param endToken The closing tag to match.
	 * @return -1 if no matching end tag is found, or the index within the string of the first
	 *  character of the end tag.
	 */
	public static int findMatchingEndTag(CharSequence content, int start, String startToken, String endToken) {
		// do some initial searching to make sure the tokens are available
		if (content == null || start < 0 || start >= content.length()) {
			return -1;
		}
		String contentString = content.toString();
		int lastEndToken = contentString.lastIndexOf(endToken);
		if (lastEndToken == -1 || lastEndToken < start) {
			return -1;
		}
		int firstStartToken = contentString.indexOf(startToken, start);
		if (firstStartToken == -1) {
			return -1;
		}
		int pos = firstStartToken;
		int count = 0;
		int nextStart = firstStartToken;
		int nextEnd = lastEndToken;
		// search for matches within the area that tokens have already been found
		while (pos >= firstStartToken && pos < (lastEndToken + endToken.length())) {
			if (nextStart != -1 && nextStart < nextEnd) {
				// cursor is currently at the match of a start token
				count++;
				pos += startToken.length();
			} else {
				// cursor is currently at the match of an end token
				count--;
				if (count == 0) {
					// this tag closes a match, return the position of the
					// start of the tag
					return pos;
				}
				pos += endToken.length();
			}
			// jump to the next start or end token
			nextEnd = contentString.indexOf(endToken, pos);
			if (nextEnd == -1) {
				// no more matching end patterns, no match
				break;
			}
			nextStart = contentString.indexOf(startToken, pos);
			pos = (nextStart == -1) ? nextEnd : Math.min(nextStart, nextEnd);
		}
		return -1;
	}

	/**
	 * Search through content, starting at a specific position, and search backwards for the
	 * first position of a matching start tag for a specified end tag.  For instance,
	 * if called with an end tag of "</b>" and a start tag of "<b>", this method
	 * will operate as follows:
	 *
	 * "01<b>567</b>23" returns 2.
	 * "01234567</b>23" returns -1.
	 *
	 * @param content The string to be searched.
	 * @param start The position within the string to start searching from (inclusive).
	 *  Only characters before this position in the string will be examined.
	 * @param startToken The opening tag to match.
	 * @param endToken The closing tag to match.
	 * @return -1 if no matching start tag is found, or the index within the string of the first
	 *  character of the start tag.
	 */
	public static int findMatchingStartTag(CharSequence content, int start, String startToken, String endToken) {
		// do some initial searching to make sure the tokens are available
		if (content == null || start < 0 || start >= content.length()) {
			return -1;
		}
		int firstStartToken = StringUtils.indexOf(content, startToken);
		if (firstStartToken == -1 || firstStartToken > start) {
			return -1;
		}
		int lastEndToken = StringUtils.lastIndexOf(content, endToken, start);
		if (lastEndToken == -1) {
			return -1;
		}
		int pos = start;
		if (pos >= (lastEndToken + endToken.length())) {
			pos = lastEndToken + endToken.length() - 1;
		}
		int count = 0;
		String contentString = content.toString();
		String substring;
		// search for matches within the area that tokens have already been found
		while (pos >= firstStartToken && pos < (lastEndToken + endToken.length())) {
			substring = contentString.substring(0, pos + 1);
			// search for matches from end-to-beginning
			if (substring.endsWith(endToken)) {
				count++;
				pos -= endToken.length();
			} else if (substring.endsWith(startToken)) {
				count--;
				if (count == 0) {
					// this tag opens a match, return the position of the
					// start of the tag
					return (pos - startToken.length()) + 1;
				}
				pos -= startToken.length();
			} else {
				pos--;
			}
		}
		return -1;
	}

	/**
	 * Given a message key and locale return a locale-specific message.
	 *
	 * @param key The message key that corresponds to the formatted message
	 *  being retrieved.
	 * @param locale The locale for the message that is to be retrieved.
	 * @return A formatted message string that is specific to the locale.
	 */
	public static String formatMessage(String key, Locale locale) {
		ResourceBundle messages = ResourceBundle.getBundle("ApplicationResources", locale);
		return messages.getString(key);
	}

	/**
	 * Given a message key, locale, and formatting parameters, return a
	 * locale-specific message.
	 *
	 * @param key The message key that corresponds to the formatted message
	 *  being retrieved.
	 * @param locale The locale for the message that is to be retrieved.
	 * @param params An array of formatting parameters to use in the message
	 *  being returned.
	 * @return A formatted message string that is specific to the locale.
	 */
	public static String formatMessage(String key, Locale locale, Object[] params) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		String message = Utilities.formatMessage(key, locale);
		formatter.applyPattern(message);
		return formatter.format(params);
	}

	/**
	 * Utility method for retrieving a key from a map, case-insensitive.  This method
	 * first tries to return an exact match, and if that fails it returns the first
	 * case-insensitive match.
	 *
	 * @param map The map being examined.
	 * @param key The key for the value being retrieved.
	 * @return A matching value for the key, or <code>null</code> if no match is found.
	 */
	public static <V> V getMapValueCaseInsensitive(Map<String, V> map, String key) {
		if (map == null) {
			return null;
		}
		if (map.containsKey(key)) {
			return map.get(key);
		}
		for (Map.Entry<String, V> entry : map.entrySet()) {
			if (StringUtils.equalsIgnoreCase(entry.getKey(), key)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Utility method to help work around XSS attacks when executing request.getQueryString().
	 * If the query string contains ", <, or > then assume it is malicious and escape.
	 *
	 * @param request The current servlet request.
	 * @return The request query string, or an escaped version if it appears to be an XSS
	 *  attack.
	 */
	public static String getQueryString(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String queryString = request.getQueryString();
		if (StringUtils.isBlank(queryString)) {
			return queryString;
		}
		if (StringUtils.containsAny(queryString, "\"><")) {
			queryString = queryString.replaceAll("\"", "%22");
			queryString = queryString.replaceAll(">", "%3E");
			queryString = queryString.replaceAll("<", "%3C");
		}
		return queryString;
	}

	/**
	 * Given a request, determine the server URL.
	 *
	 * @return A Server URL of the form http://www.example.com/
	 */
	public static String getServerUrl(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
	}

	/**
	 * Initialize a hash map from a list of strings for use in lookups.  The performance
	 * of a lookup against a hash map is superior to that of a list, so this approach is
	 * useful for critical-path lookups.
	 */
	public static Map<String, String> initializeLookupMap(String... args) {
		Map<String, String> lookupMap = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			lookupMap.put(args[i], args[i]);
		}
		return lookupMap;
	}

	/**
	 * Utility method for determining common elements in two Map objects.
	 */
	public static <K, V> Map<K, V> intersect(Map<K, V> map1, Map<K, V> map2) {
		if (map1 == null || map2 == null) {
			throw new IllegalArgumentException("Utilities.intersection() requires non-null arguments");
		}
		Map<K, V> result = new HashMap<K, V>();
		for (Map.Entry<K, V> entry : map1.entrySet()) {
			if (ObjectUtils.equals(entry.getValue(), map2.get(entry.getKey()))) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Given a string, determine if it is a valid HTML entity (such as &trade; or
	 * &#160;).
	 *
	 * @param text The text that is being examined.
	 * @return <code>true</code> if the text is a valid HTML entity.
	 */
	public static boolean isHtmlEntity(String text) {
		if (text == null) {
			return false;
		}
		// see if it was successfully converted, in which case it is an entity
		try {
			return (!text.equals(StringEscapeUtils.unescapeHtml4(text)));
		} catch (IllegalArgumentException e) {
			// "&#xffffff;" seems to be throwing errors
			return false;
		}
	}

	/**
	 * Determine if the given string is a valid IPv4 or IPv6 address.  This method
	 * uses pattern matching to see if the given string could be a valid IP address.
	 *
	 * @param ipAddress A string that is to be examined to verify whether or not
	 *  it could be a valid IP address.
	 * @return <code>true</code> if the string is a value that is a valid IP address,
	 *  <code>false</code> otherwise.
	 */
	public static boolean isIpAddress(String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return false;
		}
		Matcher m1 = Utilities.VALID_IPV4_PATTERN.matcher(ipAddress);
		if (m1.matches()) {
			return true;
		}
		Matcher m2 = Utilities.VALID_IPV6_PATTERN.matcher(ipAddress);
		return m2.matches();
	}

	/**
	 * Convert a list to a delimited string.
	 *
	 * @param list The list to convert to a string.
	 * @param delimiter The string to use as a delimiter.
	 * @return A string consisting of the delimited list items, or <code>null</code> if the
	 *  list is <code>null</code> or empty.
	 */
	public static String listToDelimitedString(List<String> list, String delimiter) {
		if (delimiter == null) {
			throw new IllegalArgumentException("Attempt to call Utilities.delimitedStringToList with no delimiter specified");
		}
		if (list == null || list.isEmpty()) {
			return null;
		}
		String result = "";
		for (String item : list) {
			if (result.length() > 0) {
				result += delimiter;
			}
			result += item;
		}
		return result;
	}

	/**
	 * Strip all HTML tags from a string.  For example, "A <b>bold</b> word" will be
	 * returned as "A bold word".  This method treats an tags that are between brackets
	 * as HTML, whether it is valid HTML or not.
	 *
	 * @param value The value that will have HTML stripped from it.
	 * @return The value submitted to this method with all HTML tags removed from it.
	 */
	public static String stripMarkup(String value) {
		return StringUtils.trim(value.replaceAll("<[^>]+>", ""));
	}
}
