<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="ui" required="false" %>
<%@ attribute name="syntaxhighlight" required="false" %>
<title>Pygmy Marmoset: ${title}</title>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/pygmymarmoset.css">
<script src="https://code.jquery.com/jquery-4.0.0.min.js"></script>
<c:if test="${ui}">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.js"></script>
</c:if>
<script src="https://code.jquery.com/ui/1.14.2/jquery-ui.min.js"></script>
<script src="${pageContext.servletContext.contextPath}/js/pygmymarmoset.js"></script>
<c:if test="${syntaxhighlight}">
<script src="${pageContext.servletContext.contextPath}/js/prism.js"></script>
<script src="${pageContext.servletContext.contextPath}/js/prism.clojure.js"></script>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/prism.css">
</c:if>