<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<html>
    <head>
        <pm:headStuff title="Log in as another user" ui="true"/>
        <script src="${pageContext.servletContext.contextPath}/js/admin_user_autocompleteon.js"></script>
    </head>
    <body data-context-path="${pageContext.servletContext.contextPath}">
        <pm:header/>
        <div id="content">
            <pm:crumbs/>
            <h1>Log in as another user</h1>
            <form action="${pageContext.servletContext.contextPath}/a/userLogin" method="post" autocomplete="off">
                <table class="formtab">
                    <tr>
                        <td class="formlabel">Username:</td>
                        <td><pm:input id="username_field" obj="creds" field="username" autocomplete="false"/></td>
                    </tr>
                    <tr>
                        <td class="formlabel"></td>
                        <td><input type="submit" name="submit" value="Log in as user"></td>
                    </tr>
                </table>
            </form>
            <pm:notification/>
        </div>
    </body>
</html>