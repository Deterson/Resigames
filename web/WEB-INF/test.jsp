<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Test</title>
</head>

<body>
<p>Ceci est une page générée depuis une JSP.</p>
<p>
    <c:out value="${ips}"/>
        <c:out value="<p>Je suis un 'paragraphe'.</p>" />

        <c:out value="<p>Je suis un 'paragraphe'.</p>" escapeXml="false" />

</p>
</body>
</html>