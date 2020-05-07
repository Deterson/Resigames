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
<script type="text/javascript">
    var ownId = '${ownId}';
</script>
<script src="../js/decrypto.js"></script>



<html ng-app="decryptoApp">
<head>
    <title>Decrypto entre bons p'tits potes</title>
</head>
<body ng-controller="decryptoCtrl">
<table>
    <tr ng-repeat="p in game.players">
        <td>{{p.name}}</td>
        <td>{{p.id}}</td>
        <td>{{p.color}}</td>
    </tr>
</table>

Vous êtes le player d'id {{playerId}}
<br />

<label>to rename: <input type="text" ng-model="renameField"></label><br/>
<button ng-click="rename()"></button>

</body>
</html>
