<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<c:set var="courseDisplayName" value="${course.name}, ${term.name} ${course.year}"/>
<html>
	<head>
		<pm:headStuff
			title="${courseDisplayName}: Project ${project.name} submission ${submission.submissionNumber} for ${student.username}"
			syntaxhighlight="true"
			/>
	</head>
	
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>${courseDisplayName}: Project ${project.name} submission ${submission.submissionNumber} for ${student.username}</h1>
			<pm:viewEntry/>
			<pm:notification/>
		</div>
	</body>
</html>
