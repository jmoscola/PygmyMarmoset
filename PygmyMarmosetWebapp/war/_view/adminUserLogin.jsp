<!DOCTYPE html>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="http://cs.ycp.edu/pygmymarmoset" prefix="pm" %>
<html>
    <head>
        <pm:headStuff title="Log in as another user" ui="true"/>
        <script type="text/javascript">
            document.addEventListener("DOMContentLoaded", function() {
                // Clear the field after Safari autofill has a chance to run
                setTimeout(function() {
                    document.getElementById("username_field").value = "";
                    document.getElementById("username_field").focus();
                    pm.autocompleteOn("#username_field", "${pageContext.servletContext.contextPath}/a/suggestUsernames");
                }, 50);
            });
        </script>
    </head>
    <body>
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