<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<html>
	<head>
		<pm:headStuff title="Admin Home"/>
	</head>
	
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>Admin Home</h1>
			<div class="navgroup">
				<ul>
				<li><a class="navlink" href="${pageContext.servletContext.contextPath}/a/createCourse">Create course</a></li>
				<li><a class="navlink" href="${pageContext.servletContext.contextPath}/a/createUser">Create user</a></li>
				<li><a class="navlink" href="${pageContext.servletContext.contextPath}/a/courses">List courses</a></li>
				<li><a class="navlink" href="${pageContext.servletContext.contextPath}/a/userLogin">Log in as another user</a></li>
				</ul>
			</div>
		</div>
	</body>
</html>
