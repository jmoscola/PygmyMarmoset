<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<html>
	<head>
		<pm:headStuff title="Create user" ui="true"/>
	</head>
	
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>Create user</h1>
			<form action="${pageContext.servletContext.contextPath}/a/createUser" method="post">
				<table class="formtab">
					<tr>
						<td class="formlabel">Username:</td>
						<td><pm:input obj="newUser" field="username"/></td>
					</tr>
					<tr>
						<td class="formlabel">First name:</td>
						<td><pm:input obj="newUser" field="firstName"/></td>
					</tr>
					<tr>
						<td class="formlabel">Last name:</td>
						<td><pm:input obj="newUser" field="lastName"/></td>
					</tr>
					<tr>
						<td class="formlabel">Password:</td>
						<td><pm:input obj="newUser" field="passwordHash" type="password" autocomplete="false"/></td>
					</tr>
					<tr>
						<td class="formlabel">Superuser:</td>
						<td><pm:input obj="newUser" field="superUser"/></td>
					</tr>
					<tr>
						<td class="formlabel"></td>
						<td><input type="submit" name="submit" value="Create user"></td>
					</tr>
				</table>
			</form>
			<pm:notification/>			
		</div>
	</body>
</html>
