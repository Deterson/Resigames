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
<div> vous êtes player {{playerId}} de couleur <b>{{playerColor}}</b></div>
<br/>
<div> state: {{state}}</div>
<br/>
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
        <label>clue for n°{{$parent.code[0]}} <b>{{$parent.words[$parent.code[0] - 1]}}</b>: <input type="text" ng-model="$parent.clues[0]"></label><br/>
        <label>clue for n°{{$parent.code[1]}} <b>{{$parent.words[$parent.code[1] - 1]}}</b>: <input type="text" ng-model="$parent.clues[1]"></label><br/>
        <label>clue for n°{{$parent.code[2]}} <b>{{$parent.words[$parent.code[2] - 1]}}</b>: <input type="text" ng-model="$parent.clues[2]"></label><br/>
        <button ng-click="sendClues()">Send clues</button>
    </div>
    <div ng-switch-when="WHITEGUESS">
        clue 1: {{$parent.game.whiteClues[0]}} <select ng-model="guesses[0]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 2: {{$parent.game.whiteClues[1]}} <select ng-model="guesses[1]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 3: {{$parent.game.whiteClues[2]}} <select ng-model="guesses[2]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>

        <button ng-click="sendGuesses()">Send guesses</button>
    </div>
    <div ng-switch-when="BLACKGUESS">
        white code was {{$parent.whiteCode}}<br/>
        clue 1: {{$parent.game.blackClues[0]}} <select ng-model="guesses[0]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 2: {{$parent.game.blackClues[1]}} <select ng-model="guesses[1]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>
        clue 3: {{$parent.game.blackClues[2]}} <select ng-model="guesses[2]"><option>1</option><option>2</option><option>3</option><option>4</option></select><br/>

        <button ng-click="sendGuesses()">Send guesses</button>
    </div>

    <div ng-switch-when="ENDROUND">
        black code was {{$parent.blackCode}}<br/>
        Round ended.<br/>
        <button ng-show="!isReady" ng-click="sendReady(true)">I'm ready</button>
        <button ng-show="isReady" ng-click="sendReady(false)">I'm not ready</button>
    </div>
    <div ng-switch-when="END">
        game finished.
        <div ng-show="game.won === 'DRAW'">it's a draw!</div>
        <div ng-show="game.won !== 'DRAW'">winner : {{$parent.game.won}}</div>
    </div>
</div>

<div>
    Mots:
    <table>
        <thead>
        <tr>
            <th>Mot #1</th>
            <th>Mot #2</th>
            <th>Mot #3</th>
            <th>Mot #4</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>{{words[0]}}</td>
            <td>{{words[1]}}</td>
            <td>{{words[2]}}</td>
            <td>{{words[3]}}</td>
        </tr>
        </tbody>
    </table>
</div>
<div>
    Score: <br/>
    Interceptions: {{yourInterceptions}}<br/>
    Malentendus  : {{yourMalentendus}}<br/>
    <br/>
    <br/>
    Adversaires:<br/>
    Interceptions: {{theirInterceptions}}<br/>
    Malentendus  : {{theirMalentendus}}<br/>
</div>

<br/>
<br/>
<div>
    <div>
        Feuille blanche
        <div ng-repeat="roundBlock in game.whiteSheet.roundBlocks">
            <b>Round {{$index}}</b>
            <div>{{roundBlock.clues[0]}} / {{roundBlock.guesses[playerColor][0]}} / {{roundBlock.code[0]}}</div>
            <div>{{roundBlock.clues[1]}} / {{roundBlock.guesses[playerColor][1]}} / {{roundBlock.code[1]}}</div>
            <div>{{roundBlock.clues[2]}} / {{roundBlock.guesses[playerColor][2]}} / {{roundBlock.code[2]}}</div>
        </div>

        <div> Liste des indices
            <div ng-repeat="x in [0, 1, 2, 3] track by $index">Mot {{x + 1}} :
                <div ng-repeat="clue in game.whiteSheet.clueLists[x].clues">{{clue}}, </div>
            </div>
        </div>
    </div>

    <div>
        Feuille noire
        <div ng-repeat="roundBlock in game.blackSheet.roundBlocks">
            <b>Round {{$index + 1}}</b>
            <div>{{roundBlock.clues[0]}} / {{roundBlock.guesses[playerColor][0]}} / {{roundBlock.code[0]}}</div>
            <div>{{roundBlock.clues[1]}} / {{roundBlock.guesses[playerColor][1]}} / {{roundBlock.code[1]}}</div>
            <div>{{roundBlock.clues[2]}} / {{roundBlock.guesses[playerColor][2]}} / {{roundBlock.code[2]}}</div>
        </div>

        <div> Liste des indices
            <div ng-repeat="x in [0, 1, 2, 3] track by $index">Mot {{x + 1}} :
                <div ng-repeat="clue in game.blackSheet.clueLists[x].clues">{{clue}}, </div>
            </div>
        </div>

    </div>
</div>
</body>
</html>
