<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<c:set var="courseDisplayName" value="${course.name}, ${term.name} ${course.year}"/>
<html>
	<head>
		<pm:headStuff
			title="${courseDisplayName}: Submission ${submission.submissionNumber} in ${project.name}"
			syntaxhighlight="true"
			/>
	</head>
	
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>${courseDisplayName}: Submission ${submission.submissionNumber} in ${project.name}</h1>
			<pm:viewEntry/>
			<pm:notification/>
		</div>
	</body>
</html>
