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
package org.jamwiki.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import org.jamwiki.Environment;
import org.jamwiki.model.TopicType;
import org.jamwiki.utils.Pagination;
import org.jamwiki.utils.WikiLogger;

/**
 * DB2-specific implementation of the QueryHandler interface.  This class implements
 * DB2-specific methods for instances where DB2 does not support the default
 * ASCII SQL syntax.
 */
public class DB2QueryHandler extends AnsiQueryHandler {

	private static final WikiLogger logger = WikiLogger.getLogger(DB2QueryHandler.class.getName());
	private static final String SQL_PROPERTY_FILE_NAME = "sql/sql.db2.properties";

	/**
	 *
	 */
	public DB2QueryHandler() {
		Properties defaults = Environment.loadProperties(AnsiQueryHandler.SQL_PROPERTY_FILE_NAME);
		Properties props = Environment.loadProperties(SQL_PROPERTY_FILE_NAME, defaults);
		super.init(props);
	}

	/**
	 *
	 */
	protected PreparedStatement getCategoriesStatement(Connection conn, int virtualWikiId, String virtualWikiName, Pagination pagination) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_CATEGORIES);
		stmt.setInt(1, virtualWikiId);
		stmt.setInt(2, pagination.getStart());
		stmt.setInt(3, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getLogItemsStatement(Connection conn, int virtualWikiId, String virtualWikiName, int logType, Pagination pagination, boolean descending) throws SQLException {
		int index = 1;
		PreparedStatement stmt = null;
		if (logType == -1) {
			stmt = conn.prepareStatement(STATEMENT_SELECT_LOG_ITEMS);
		} else {
			stmt = conn.prepareStatement(STATEMENT_SELECT_LOG_ITEMS_BY_TYPE);
			stmt.setInt(index++, logType);
		}
		stmt.setInt(index++, virtualWikiId);
		stmt.setInt(index++, pagination.getStart());
		stmt.setInt(index++, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getRecentChangesStatement(Connection conn, String virtualWiki, Pagination pagination, boolean descending) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_RECENT_CHANGES);
		stmt.setString(1, virtualWiki);
		stmt.setInt(2, pagination.getStart());
		stmt.setInt(3, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getTopicHistoryStatement(Connection conn, int topicId, Pagination pagination, boolean descending, boolean selectDeleted) throws SQLException {
		// the SQL contains the syntax "is {0} null", which needs to be formatted as a message.
		Object[] params = {""};
		if (selectDeleted) {
			params[0] = "not";
		}
		String sql = this.formatStatement(STATEMENT_SELECT_TOPIC_HISTORY, params);
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, topicId);
		stmt.setInt(2, pagination.getStart());
		stmt.setInt(3, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getTopicsAdminStatement(Connection conn, int virtualWikiId, Pagination pagination) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_TOPICS_ADMIN);
		stmt.setInt(1, virtualWikiId);
		stmt.setInt(2, pagination.getStart());
		stmt.setInt(3, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getUserContributionsByLoginStatement(Connection conn, String virtualWiki, String login, Pagination pagination, boolean descending) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_WIKI_USER_CHANGES_LOGIN);
		stmt.setString(1, virtualWiki);
		stmt.setString(2, login);
		stmt.setInt(3, pagination.getStart());
		stmt.setInt(4, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getUserContributionsByUserDisplayStatement(Connection conn, String virtualWiki, String userDisplay, Pagination pagination, boolean descending) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_WIKI_USER_CHANGES_ANONYMOUS);
		stmt.setString(1, virtualWiki);
		stmt.setString(2, userDisplay);
		stmt.setInt(3, pagination.getStart());
		stmt.setInt(4, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement getWatchlistStatement(Connection conn, int virtualWikiId, int userId, Pagination pagination) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_WATCHLIST_CHANGES);
		stmt.setInt(1, virtualWikiId);
		stmt.setInt(2, userId);
		stmt.setInt(3, pagination.getStart());
		stmt.setInt(4, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement lookupTopicByTypeStatement(Connection conn, int virtualWikiId, TopicType topicType1, TopicType topicType2, int namespaceStart, int namespaceEnd, Pagination pagination) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_TOPIC_BY_TYPE);
		stmt.setInt(1, virtualWikiId);
		stmt.setInt(2, topicType1.id());
		stmt.setInt(3, topicType2.id());
		stmt.setInt(4, namespaceStart);
		stmt.setInt(5, namespaceEnd);
		stmt.setInt(6, pagination.getStart());
		stmt.setInt(7, pagination.getEnd());
		return stmt;
	}

	/**
	 *
	 */
	protected PreparedStatement lookupWikiUsersStatement(Connection conn, Pagination pagination) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(STATEMENT_SELECT_WIKI_USERS);
		stmt.setInt(1, pagination.getStart());
		stmt.setInt(2, pagination.getEnd());
		return stmt;
	}
}
