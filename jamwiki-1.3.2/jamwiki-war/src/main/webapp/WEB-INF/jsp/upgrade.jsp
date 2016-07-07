<%--

  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, version 2.1, dated February 1999.

  This program is free software; you can redistribute it and/or modify
  it under the terms of the latest version of the GNU Lesser General
  Public License as published by the Free Software Foundation;

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program (LICENSE.txt); if not, write to the Free Software
  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

--%>
<%@ page errorPage="/WEB-INF/jsp/error.jsp"
    contentType="text/html; charset=utf-8"
%>

<%@ include file="page-init.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><jamwiki_t:wikiMessage message="${pageInfo.pageTitle}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style>
body {
	background: #f9f9f9;
	color: black;
	padding: 5px;
}
body, input, select {
	font: 95% sans-serif, tahoma;
}
#upgrade-container {
	margin: 20px auto;
	width: 800px;
	padding: 10px 5px;
}
#upgrade-table {
	border: 2px solid #333333;
	padding: 10px;
	width: 95%;
}
#upgrade-messages {
	font: verdana, helvetica, sans-serif;
	padding: 10px 0;
}
.red {
	color: #ff0000;
}
.green {
	color: #009900;
}
</style>
</head>
<body>
<div id="upgrade-container">

<h3><jamwiki_t:wikiMessage message="${pageInfo.pageTitle}" /></h3>

<form name="adminUpgrade" method="post">
<input type="hidden" name="function" value="upgrade" />
<table id="upgrade-table">
<c:if test="${!empty successMessage || !empty pageInfo.errors || !empty pageInfo.messages}">
	<tr>
		<td colspan="2">
			<div id="upgrade-messages">
				<c:if test="${!empty successMessage}"><h4><jamwiki_t:wikiMessage message="${successMessage}" /></h4></c:if>
				<c:if test="${!empty pageInfo.errors}">
				<c:forEach items="${pageInfo.errors}" var="message">
					<div class="red"><jamwiki_t:wikiMessage message="${message}" /></div>
				</c:forEach>
				</c:if>
				<c:if test="${!empty pageInfo.messages}">
				<c:forEach items="${pageInfo.messages}" var="message">
					<div class="green"><jamwiki_t:wikiMessage message="${message}" /></div>
				</c:forEach>
				</c:if>
			</div>
		</td>
	</tr>
</c:if>
<c:if test="${empty successMessage && empty failure}">
	<tr><td colspan="2"><fmt:message key="upgrade.caption.detected" /></td></tr>
	<c:if test="${!empty upgradeDetails}">
		<tr>
			<td colspan="2">
				<ul>
					<c:forEach items="${upgradeDetails}" var="upgradeDetail">
						<li><jamwiki_t:wikiMessage message="${upgradeDetail}" /></li>
					</c:forEach>
				</ul>
			</td>
		</tr>
	</c:if>
	<tr><td colspan="2" align="center"><input type="submit" name="button" /></td></tr>
</c:if>
</table>
</form>

</div>
</body>
</html>
