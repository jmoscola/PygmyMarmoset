<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<html>
	<head>
		<pm:headStuff title="Log in"/>
		<script src="${pageContext.servletContext.contextPath}/js/login_autofocus.js"></script>
	</head>
	
	<body id="login-page">
		<div id="loginbox">
			<h1>Welcome to Pygmy Marmoset!</h1>
			<p>Please enter your username and password:</p>
			<form action="${pageContext.servletContext.contextPath}/login" method="post">
				<table class="formtab">
					<tr>
						<td class="formlabel">Username:</td>
						<td><pm:input id="username_field" obj="creds" field="username"/></td>
					</tr>
					<tr>
						<td class="formlabel">Password:</td>
						<td><pm:input obj="creds" field="password" type="password"/></td>
					</tr>
					<tr>
						<td class="formlabel"></td>
						<td><input type="submit" name="submit" value="Log in"></input></td>
					</tr>
				</table>
				<input type="hidden" name="creds.goal" value="${fn:escapeXml(creds.goal)}"></input>
			</form>

			<pm:notification/>			
		</div>
	</body>
</html>
