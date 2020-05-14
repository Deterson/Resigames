let decryptoApp = angular.module('decryptoApp', []);

decryptoApp.controller('decryptoCtrl', ['$scope', function ($scope) {

    // steps of game.step
    let SETUP = "SETUP";
    let CLUEWRITING = "CLUEWRITING";
    let WHITEGUESS = "WHITEGUESS";
    let BLACKGUESS = "BLACKGUESS";
    let ENDROUND = "ENDROUND";
    let END = "END";

/*  states of $scope.state:
    SETUP
    WAIT
    CLUEWRITING
    WHITEGUESS
    BLACKGUESS
    ENDROUND
    */

    $scope.ownId = ownId;

    $scope.game = {};
    $scope.game.players = [];

    $scope.playerId = null;
    $scope.state = "setup";

    $scope.isReady = false;

    function resetCodes()
    {
        $scope.code = null;
        $scope.whiteCode = null;
        $scope.blackCode = null;
    }

    function resetClues()
    {
        $scope.clues = ['', '', ''];
    }

    function resetGuesses()
    {
        $scope.guesses = [];
    }

    resetCodes();
    resetClues();
    resetGuesses();

    $scope.renameField = "";

    let socket = null;

    function launchGame() {
    }

    function findPlayerFromId(id) {
        return $scope.game.players.find(p => p.id === id);
    }

    function getClientPlayer() {
        return findPlayerFromId($scope.playerId);
    }

    function handleUpdate(game) {
        $scope.game = game;
        changeState();
    }

    function changeState()
    {
        switch ($scope.game.step) {
            case SETUP :
                $scope.state = "SETUP";
                break;
            case CLUEWRITING:
                if ($scope.game.whiteCluer.id === $scope.playerId
                    || $scope.game.blackCluer.id === $scope.playerId)
                    $scope.state = "CLUEWRITING";
                else
                    $scope.state = "WAIT";
                break;

            case WHITEGUESS:
                if (getClientPlayer().color === 'WHITE') // délibérément verbalisé pour un potentiel FOREIGN_GUESS
                {
                    if ($scope.game.whiteCluer.id === $scope.playerId)
                        $scope.state = 'WAIT';
                    else
                        $scope.state = 'WHITEGUESS';
                }
                else
                    $scope.state = 'WHITEGUESS'; // peut-être un FOREIGN GUESS là plus tard

                break;

            case BLACKGUESS:
                if (getClientPlayer().color === 'BLACK') {
                    if ($scope.game.blackCluer.id === $scope.playerId)
                        $scope.state = 'WAIT';
                    else
                        $scope.state = 'BLACKGUESS';
                    resetGuesses();
                }
                else
                    $scope.state = 'BLACKGUESS'; // peut-être un FOREIGN GUESS là plus tard
                break;

            case ENDROUND:
                $scope.state = "ENDROUND";
                break;

            case END:
                $scope.state = 'END';
        }

    }

    function connect(name)
    {
        if (name === undefined || name === "")
            name = "anonyme";

        let preurl = window.location.protocol === 'http:' ? 'ws://' : 'wss://';
        let url = preurl + window.location.host + '/websocket/decrypto?requestSessionId=' + ownId;

        if ('WebSocket' in window)
            socket = new WebSocket(url);
        else
            alert('Error: WebSocket is not supported by this browser.');

        socket.onopen = function() {
            console.log("oui opené");
            launchGame();
        };

        socket.onclose = function() {
            console.log("nonon closed");
        };

        socket.onmessage = function(message) {
            let packet = JSON.parse(message.data);
            console.log(packet);
            switch (packet.type) {
                case 'yourPlayerId':
                    $scope.playerId = packet.id;
                    break;
                case 'rename':
                    findPlayerFromId(packet.player.id).name = packet.newName;
                    break;
                case 'changeColor':
                    findPlayerFromId(packet.player.id).color = packet.color;
                    break;
                case 'update':
                    handleUpdate(packet.game);
                    break;
                case 'changeStep':
                    $scope.game.step = packet.step;
                    break;
                case 'code':
                    console.log("code!");
                    if (packet.color === null)
                        $scope.code = packet.code;
                    else if (packet.color === 'WHITE')
                        $scope.whiteCode = packet.code;
                    else
                        $scope.blackCode = packet.code;
                    break;
            }

            $scope.$apply();
            console.log($scope.game);
        };
    }

    $scope.rename = function() {
        if (!isAlphaNumerical($scope.renameField))
            return;
        let packet = {};
        packet.type = 'rename';
        packet.newName = $scope.renameField;
        socket.send(JSON.stringify(packet));
    };

    $scope.changeColor = function() {
        let packet = {};
        packet.type = 'changeColor';
        packet.color = (getClientPlayer().color === 'WHITE' ? 'BLACK' : 'WHITE');
        socket.send(JSON.stringify(packet));
    };

    $scope.startGame = function() {
        let packet = {};
        packet.type = 'start';
        socket.send(JSON.stringify(packet));
    };

    $scope.sendClues = function()
    {
        let packet = {};
        packet.type = 'clues';
        packet.clues = $scope.clues;
        socket.send(JSON.stringify(packet));
    };

    $scope.sendGuesses = function()
    {
        let packet = {};
        packet.type = "guess";
        packet.guesses = $scope.guesses;
        socket.send(JSON.stringify(packet));
    };

    $scope.sendReady = function(ready)
    {
        $scope.isReady = !$scope.isReady;
        let packet = {};
        packet.type = "ready";
        packet.ready = ready;
        socket.send(JSON.stringify(packet));
    };

    function isAlphaNumerical(input)
    {
        if (/^[a-z0-9]+$/i.test(input))
            return true;
        if (input === undefined || input === null || input === "")
            alert("valeur non vide pls");
        else
            alert("only alphanumerical characters (ouais pélo c'est pas pour tout de suite l'injection SQL)");
        return false;
    }

    connect();

}]);