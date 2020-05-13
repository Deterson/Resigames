<%--
  Created by IntelliJ IDEA.
  User: drde6
  Date: 05/05/2020
  Time: 23:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    table {
        border-collapse: collapse;
    }

    table, td, th {
        border: 1px solid black;
    }
</style>

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
<div> state: {{state}}</div>

<div ng-switch="state">

    <div ng-switch-when="SETUP">
        <table>
            <tr ng-repeat="p in game.players">
                <td>{{p.name}}</td>
                <td>{{p.id}}</td>
                <td>{{p.color}}</td>
            </tr>
        </table>

        Vous êtes le player d'id {{playerId}}
        <br /><br /><br /><br />

        <label>to rename: <input type="text" ng-model="$parent.renameField"><button ng-click="rename()"></button></label><br/>
        <label>Change side<button ng-click="changeColor()"></button></label><br/>
        <label>Start<button ng-click="startGame()"></button></label><br/>

    </div>
    <div ng-switch-when="CLUEWRITING">
        <label>clue 1: <input type="text" ng-model="$parent.clues[0]"></label><br/>
        <label>clue 2: <input type="text" ng-model="$parent.clues[1]"></label><br/>
        <label>clue 3: <input type="text" ng-model="$parent.clues[2]"></label><br/>
        <button ng-click="sendClues()">Send clues</button>
    </div>
    <div ng-switch-when="WHITEGUESS">
        clue 1: {{$parent.game.whiteClues[0]}} <select ng-model="guesses[0]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 2: {{$parent.game.whiteClues[1]}} <select ng-model="guesses[1]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 3: {{$parent.game.whiteClues[2]}} <select ng-model="guesses[2]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>

        <button ng-click="sendGuesses()">Send guesses</button>
    </div>
    <div ng-switch-when="BLACKGUESS">
        clue 1: {{$parent.game.blackClues[0]}} <select ng-model="guesses[0]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 2: {{$parent.game.blackClues[1]}} <select ng-model="guesses[1]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 3: {{$parent.game.blackClues[2]}} <select ng-model="guesses[2]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>

        <button ng-click="sendGuesses()">Send guesses</button>
    </div>

</div>
</body>
</html>
