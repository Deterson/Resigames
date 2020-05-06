<%--
  Created by IntelliJ IDEA.
  User: drde6
  Date: 05/05/2020
  Time: 23:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular-route.js"></script>




<html ng-app="decryptoApp">
<head>
    <title>Decrypto entre bons p'tits potes</title>
</head>
<body ng-controller="decryptoCtrl">
<label>Mon ID:</label>
{{ownId}}
<br />




<script type="text/javascript">
    var ownId = '${ownId}';
</script>
<script src="../js/decrypto.js"></script>
</body>
</html>
