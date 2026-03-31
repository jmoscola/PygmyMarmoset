<%@ page isErrorPage="true" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.ycp.cs.pygmymarmoset.app.model.ErrorMessage" %>
<%
    int statusCode = 500;
    Object statusAttr = request.getAttribute("jakarta.servlet.error.status_code");
    if (statusAttr != null) {
        statusCode = (Integer) statusAttr;
    }

    String title;
    switch (statusCode) {
        case 400: title = "Bad Request"; break;
        case 403: title = "Access Forbidden"; break;
        case 404: title = "Not Found"; break;
        default:  title = "An Error Occurred"; break;
    }

    ErrorMessage errmsg = new ErrorMessage();
    errmsg.setText("An unexpected error has occurred. Please try again or contact your system administrator.");

    request.setAttribute("httpStatus", statusCode);
    request.setAttribute("title", title);
    request.setAttribute("errmsg", errmsg);
%>
<jsp:forward page="/_view/errorResponse.jsp" />