<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<c:set var="courseDisplayName" value="${course.name}, ${term.name} ${course.year}"/>
<html>
	<head>
		<pm:headStuff title="${courseDisplayName}: Project ${project.name}"/>
	</head>
	
	<body>
		<pm:header/>
		<div id="content">
			<pm:crumbs/>
			<h1>${courseDisplayName}: Project ${project.name}</h1>
			<!--
			id=${project.id} courseId=${project.courseId} name=${project.name} description=${project.description} visible=${project.visible}
			-->
			<h2>Activity</h2>
			<table class="objtable">
				<thead>
					<tr>
						<th>Student</th>
						<th>Submissions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${studentActivity}" var="p">
						<tr>
							<td><a href="${pageContext.servletContext.contextPath}/i/submissions/${course.id}/${project.id}/${p.first.id}">${p.first.lastName}, ${p.first.firstName}</a></td>
							<td style="text-align: right;">${p.second}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<h2>Visibility</h2>
			<div>
				Project is ${project.visible ? "visible" : "not visible"}
				<form style="display: inline;" action="${pageContext.servletContext.contextPath}/i/project/${course.id}/${project.id}" method="post">
					<input type="submit" name="toggle" value="Toggle visibility">
				</form>
			</div>
			<h2>Options</h2>
			<div class="navgroup">
				<ul>
				<li><a class="navlink" href="${pageContext.servletContext.contextPath}/i/editProject/${course.id}/${project.id}">Edit project</a></li>
				</ul>
			</div>
		</div>
	</body>
</html>
