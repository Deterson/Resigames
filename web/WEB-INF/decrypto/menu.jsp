<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="style/decrypto.css">

<!-- Libraries for Bootstrap -->

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<!-- Popper JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.13.0/css/all.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.13.0/css/v4-shims.css">

<!-- Previous Scripts -->

<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular-route.js"></script>
<script type="text/javascript">
    var ownId = '${ownId}';
</script>

<html>

<head>
    <title>Decrypto</title>
</head>

<body>

<div class="container-fluid">

    <p class="display-1 text-center">Decrypto</p>

    <div class="row">

        <div class="col-sm-6">

            <p class="display-4 text-center">Liste des parties</p>

            <div class="game-container">

                <div class="game">
                    <div class="back">
                        <p class="status">Status</p>
                        <p class="host">Créé par Andrei</p>
                        <p class="player">5 Joueurs</p>
                        <p class="time">1:15<i class="fa fa-hourglass-end ml-2"></i></p>
                    </div>
                    <div class="front">
                        <button type="button" class="btn btn-primary">Rejoindre</button>
                    </div>
                </div>

                <div class="game">
                    <div class="back">
                        <p class="status">En cours</p>
                        <p class="host">Créé par Fife</p>
                        <p class="player">4 Joueurs</p>
                        <p class="time">15:03<i class="fa fa-hourglass-end ml-2"></i></p>
                    </div>
                    <div class="front">
                        <button type="button" class="btn btn-primary">Regarder</button>
                    </div>
                </div>

                <div class="game">
                    <div class="back">
                        <p class="status">Préparation</p>
                        <p class="host">Créé par Louis</p>
                        <p class="player">3 Joueurs</p>
                        <p class="time">0:30<i class="fa fa-hourglass-end ml-2"></i></p>
                    </div>
                    <div class="front">
                        <button type="button" class="btn btn-primary">Rejoindre</button>
                    </div>
                </div>

                <div class="game">
                    <div class="back">
                        <p class="status">Préparation</p>
                        <p class="host">Créé par Louis</p>
                        <p class="player">3 Joueurs</p>
                        <p class="time">0:30<i class="fa fa-hourglass-end ml-2"></i></p>
                    </div>
                    <div class="front">
                        <button type="button" class="btn btn-primary">Rejoindre</button>
                    </div>
                </div>

                <div class="game">
                    <div class="back">
                        <p class="status">Préparation</p>
                        <p class="host">Créé par Louis</p>
                        <p class="player">3 Joueurs</p>
                        <p class="time">0:30<i class="fa fa-hourglass-end ml-2"></i></p>
                    </div>
                    <div class="front">
                        <button type="button" class="btn btn-primary">Rejoindre</button>
                    </div>
                </div>

                <div class="game">
                    <div class="add">
                        <div class='horizontal-plus'></div>
                        <div class='vertical-plus'></div>
                    </div>
                </div>

            </div>

        </div>

        <div class="col-sm-6">

            <p class="display-4 text-center">Règles</p>

            <img src="img/rules.png" height="570px">
        </div>

    </div>
</div>

</body>
</html>
