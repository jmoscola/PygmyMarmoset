<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<c:set var="courseDisplayName" value="${course.name}, ${term.name} ${course.year}" scope="request"/>
<html>
    <head>
        <pm:headStuff title="${courseDisplayName}: Edit project ${project.name}" ui="true"/>
        <script src="${pageContext.servletContext.contextPath}/js/init_datetime_picker.js"></script>
    </head>
    <body>
        <pm:header/>
        <div id="content">
            <pm:crumbs/>
            <h1>${courseDisplayName}: Edit project ${project.name}</h1>
            <pm:editProject actionuri="/i/editProject/${course.id}/${project.id}" submitlabel="Update project"/>
            <pm:notification/>
        </div>
    </body>
</html>