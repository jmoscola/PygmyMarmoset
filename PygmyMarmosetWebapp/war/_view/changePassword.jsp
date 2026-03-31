<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<html>
	<head>
		<pm:headStuff title="Change password for ${user.username}"/>
	</head>
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>Change password for ${user.username}</h1>
			<form action="${pageContext.servletContext.contextPath}/passwd" method="post">
				<table class="formtab">
					<tr>
						<td class="formlabel">New password:</td>
						<td><pm:input obj="updatedPasswd" field="password" type="password" autocomplete="false"/></td>
					</tr>
					<tr>
						<td class="formlabel">Confirm new password:</td>
						<td><pm:input obj="updatedPasswd" field="confirm" type="password" autocomplete="false"/></td>
					</tr>
					<tr>
						<td></td>
						<td><input type="submit" name="submit" value="Change password"></td>
					</tr>
				</table>
			</form>
			<pm:notification/>
		</div>
	</body>
</html>
