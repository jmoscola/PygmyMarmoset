<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<c:set var="courseDisplayName" value="${course.name}, ${term.name} ${course.year}"/>
<html>
	<head>
		<pm:headStuff title="${courseDisplayName}: Project ${project.name} submissions for ${student.username}"/>
	</head>
	
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>${courseDisplayName}: Project ${project.name} submissions for ${student.username}</h1>
			<h2>Submissions</h2>
			<pm:submissionTable
				linkpfx="${pageContext.servletContext.contextPath}/i/submission/${course.id}/${project.id}/${student.id}"
				downloadpfx="${pageContext.servletContext.contextPath}/i/download/${course.id}/${project.id}/${student.id}"
				/>
		</div>
	</body>
</html>
