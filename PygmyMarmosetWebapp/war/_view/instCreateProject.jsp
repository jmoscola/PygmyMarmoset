<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<c:set var="courseDisplayName" value="${course.name}, ${term.name} ${course.year}" scope="request"/>
<html>
    <head>
        <pm:headStuff title="${courseDisplayName}: Create Project" ui="true"/>
        <script type="text/javascript">
            document.addEventListener("DOMContentLoaded", function() {
                pm.initDateTimePicker("#proj-ontime");
                pm.initDateTimePicker("#proj-late");
            });
        </script>
    </head>
    <body>
        <pm:header/>
        <div id="content">
            <pm:crumbs/>
            <h1>Create project in ${courseDisplayName}</h1>
            <pm:editProject actionuri="/i/createProject/${course.id}" submitlabel="Create project"/>
            <pm:notification/>
        </div>
    </body>
</html>