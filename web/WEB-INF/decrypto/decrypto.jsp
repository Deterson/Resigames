<%--
  Created by IntelliJ IDEA.
  User: drde6
  Date: 05/05/2020
  Time: 23:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="/style/decrypto.css">

<!-- Libraries for Bootstrap -->

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<!-- Popper JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"></script>

<!-- Previous Scripts -->

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

<!-- Menu and State -->

<nav class="navbar navbar-expand-sm bg-light">

    <div class="navbar-brand">
        <img src="img/logo_2.jpg"  style="height:50px">
    </div>

    <span class="navbar-text px-2">{{state}}</span>
    <span class="navbar-text px-2">Joueur {{playerId}}</span>
    <span class="navbar-text px-2">Equipe {{playerColor}}</span>
    <span class="navbar-text" style="text-align: center">{{stateText}}</span>

    <ul class="navbar-nav ml-auto">
        <li><a class="px-2" ng-click="testToasts()">Test Toasts</a></li>
        <li><a id="btnModal" class="px-2" data-toggle="modal" data-target="#modal">Test Modal</a></li>
        <li><a class="px-2" ng-click="openNav()">Joueurs ({{game.players.length}})</a></li>
    </ul>
</nav>

<div>
    <!-- Words and Score -->

    <div class="card  basic-block">
        <div class="row">

            <div class="col-sm-2">
                <h4>Score de votre équipe:</h4>
                <p><b>Interceptions:</b> {{yourInterceptions}}</p>
                <p><b>Malentendus:</b> {{yourMalentendus}}</p>
            </div>

            <div class="col-sm-8">
                <table class="table">
                    <thead>
                    <tr>
                        <th class="word-header">Mot #1</th>
                        <th class="word-header">Mot #2</th>
                        <th class="word-header">Mot #3</th>
                        <th class="word-header">Mot #4</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><div class="word-container"><div class="label-danger word-label">{{words[0]}}</div></div></td>
                        <td><div class="word-container"><div class="label-danger word-label">{{words[1]}}</div></div></td>
                        <td><div class="word-container"><div class="label-danger word-label">{{words[2]}}</div></div></td>
                        <td><div class="word-container"><div class="label-danger word-label">{{words[3]}}</div></div></td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <div class="col-sm-2">
                <h4>Score des adversaires:</h4>
                <p><b>Interceptions:</b> {{theirInterceptions}}</p>
                <p><b>Malentendus:</b> {{theirMalentendus}}</p>
            </div>
        </div>
    </div>

    <!-- Interface -->

    <div class="card basic-block">

        <div class="row">

            <div class="col-sm-3">
                <div class="card-container">
                    <img ng-src="img/{{cardPngPath}}" class="img-thumbnail" style="height: 250px;">
                    <span ng-show="game.whiteCluer.id === playerId || game.blackCluer.id === playerId">{{code[0] + '.' + code[1] + '.' + code[2]}}</span>
                </div>

                <div ng-show="state === 'CLUEWRITING'" class="progress mt-2" style="margin: auto">
                    <div id="timer" class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>

            <div class="col-sm-3" ng-disabled="state !== 'CLUEWRITING'">
                <div>

                    <div style="height: 200px;">
                        <label>Indice <b>n°{{code[0]}}</b>: <input type="text" ng-model="inputClues[0]"></label>
                        <label>Indice <b>n°{{code[1]}}</b>: <input type="text" ng-model="inputClues[1]"></label>
                        <label>Indice <b>n°{{code[2]}}</b>: <input type="text" ng-model="inputClues[2]"></label>
                    </div>

                    <div class="row word-container">
                        <button class="btn btn-primary" ng-click="sendClues()">Envoyer</button>
                    </div>
                </div>
            </div>
            <div class="col-sm-3" ng-disabled="state !== 'WHITEGUESS' && state !== 'BLACKGUESS'">
                <div>
                    <div style="height: 200px;">
                        <label> Indice : <b ng-show="clues[0]">"{{clues[0]}}"</b><br/>
                            <select ng-model="guesses[0]" ng-options="n for n in numbers"></select>
                        </label><br/>
                        <label> Indice : <b ng-show="clues[1]">"{{clues[1]}}"</b><br/>
                            <select ng-model="guesses[1]" ng-options="n for n in numbers"></select>
                        </label><br/>
                        <label> Indice : <b ng-show="clues[2]">"{{clues[2]}}"</b><br/>
                            <select ng-model="guesses[2]" ng-options="n for n in numbers"></select>
                        </label><br/>
                    </div>
                    <div class="row word-container">
                        <button class="btn btn-primary" ng-click="sendGuesses()">Envoyer</button>
                    </div>
                </div>
            </div>



        </div>

        <div ng-show="state === 'ENDROUND'">
            <div class="row">
                <div class="col-sm-12">
                    <div class="word-container">
                        <div>
                            <h4 style="text-align: center">Round ended</h4>
                            <button class="btn btn-primary btn-lg" ng-show="!isReady" ng-click="sendReady(true)">I'm ready</button>
                            <button class="btn btn-primary btn-lg" ng-show="isReady" ng-click="sendReady(false)">I'm not ready</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <!-- Player's Sheet -->

    <div class="card  basic-block">
        <div class="row">

            <div class="col-sm-6">
                <div class="sheet white-sheet">
                    <h3>{{game.whiteName}}</h3>

                    <table class="table word-header">
                        <thead>
                        <tr>
                            <th>Mot #1</th>
                            <th>Mot #2</th>
                            <th>Mot #3</th>
                            <th>Mot #4</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="clues in whiteClueList">
                            <td>{{clues[0]}}</td>
                            <td>{{clues[1]}}</td>
                            <td>{{clues[2]}}</td>
                            <td>{{clues[3]}}</td>
                        </tr>
                        </tbody>
                    </table>

                    <div style="display: flex; flex-wrap: wrap">
                        <div ng-repeat="rb in game.whiteSheet.roundBlocks" class="round-block">
                            <b>Round {{$index}}</b>
                            <div>{{rb.clues[0]}} / {{rb.guesses[playerColor][0]}} / {{rb.code[0]}}</div>
                            <div>{{rb.clues[1]}} / {{rb.guesses[playerColor][1]}} / {{rb.code[1]}}</div>
                            <div>{{rb.clues[2]}} / {{rb.guesses[playerColor][2]}} / {{rb.code[2]}}</div>
                        </div>
                    </div>
                </div>

            </div>

            <div class="col-sm-6">
                <div class="sheet black-sheet">
                    <h3>{{game.blackName}}</h3>

                    <table class="table word-header">
                        <thead>
                        <tr>
                            <th>Mot #1</th>
                            <th>Mot #2</th>
                            <th>Mot #3</th>
                            <th>Mot #4</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="clues in blackClueList">
                            <td>{{clues[0]}}</td>
                            <td>{{clues[1]}}</td>
                            <td>{{clues[2]}}</td>
                            <td>{{clues[3]}}</td>
                        </tr>
                        </tbody>
                    </table>

                    <div style="display: flex; flex-wrap: wrap">
                        <div ng-repeat="rb in game.blackSheet.roundBlocks" class="round-block">
                            <b>Round {{$index}}</b>
                            <div>{{rb.clues[0]}} / {{rb.guesses[playerColor][2]}} / {{rb.code[0]}}</div>
                            <div>{{rb.clues[1]}} / {{rb.guesses[playerColor][2]}} / {{rb.code[1]}}</div>
                            <div>{{rb.clues[2]}} / {{rb.guesses[playerColor][2]}} / {{rb.code[2]}}</div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- Player's Modal -->

<div id="modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-body">
                <div style="text-align: center">
                    <span>{{modal.message}}</span>
                    <img ng-src="img/{{modal.picPath}}"><br>
                    <button ng-show="modal.hasButton" type="button" class="btn btn-secondary" data-dismiss="modal">{{modal.buttonMessage}}</button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Popups -->

<div style="position: fixed; bottom: 0; right: 0;">

    <div id="toast-1" class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="width: 450px">
        <div class="toast-header">
            <strong class="mr-auto">Fife</strong>
            <small class="mr-auto"> Message</small>
            <small class="text-muted">10s</small>
            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="toast-body">
            Yo, chaud pour recommencer
        </div>
    </div>

    <div id="toast-2" class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="width: 450px">
        <div class="toast-header">
            <strong class="mr-auto">Andrei</strong>
            <small class="mr-auto"> Message</small>
            <small class="text-muted">just now</small>
            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="toast-body">
            Ouais, carrément
        </div>
    </div>

    <div id="toast-3" class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="width: 450px">
        <div class="toast-header">
            <strong class="mr-auto">Interception</strong>
            <small class="mr-auto"> Alerte</small>
            <small class="text-muted">25s</small>
            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="toast-body">
            Votre code vient d'être intercepté par l'équipe adverse
        </div>
    </div>

</div>

<!-- Players and Setup -->

<div id="mySidebar" class="sidebar">
    <a href="javascript:void(0)" class="closebtn" ng-click="closeNav()">&times;</a>

    <div style="width: 500px">
        <h4>Liste des joueurs</h4>

        <table class="player-table">
            <tr ng-repeat="p in game.players">
                <td>{{p.name}}</td>
                <td>{{p.id}}</td>
                <td>{{p.color}}</td>
            </tr>
        </table>

        <div class="mx-3">
            <label>Vous renommer : <input type="text" ng-model="renameField"><button class="btn btn-secondary mx-3" ng-click="rename()">Valider</button></label><br/>
            <label>Renommer l'équipe : <input type="text" ng-model="renameTeamField"><button class="btn btn-secondary mx-3" ng-click="renameTeam()">Valider</button></label><br/>
            <button class="btn btn-warning" ng-click="changeColor()">Changer d'équipe</button><br/>
            <button class="btn btn-success my-3" ng-show="game.step === 'SETUP'" ng-click="startGame()">Débuter</button>
        </div>

        <h4>Discussion</h4>

    </div>

</div>


</body>
</html>
