let decryptoApp = angular.module('decryptoApp', []);

decryptoApp.controller('decryptoCtrl', ['$scope', function ($scope) {

    $scope.ownId = ownId;

    $scope.game = {};
    $scope.game.players = [];
    $scope.playerId = null;

    $scope.renameField = "";

    let socket = null;

    function launchGame() {
    }

    function findPlayerFromId(id) {
        return $scope.game.players.find(p => p.id === id);
    }

    function getCurrentPlayer() {
        return findPlayerFromId($scope.playerId);
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
                    findPlayerFromId(packet.playerId).name = packet.newName;
                    break;
                case 'changeColor':
                    findPlayerFromId(packet.playerId).color = packet.color;
                    break;
                case 'update':
                    $scope.game = packet.game;
                    break;
            }

            $scope.$apply();
            console.log($scope.game);
        };
    }

    $scope.rename = function() {
        if (!isAlphaNumerical($scope.renameField))
            return;
        socket.send('{"type":"rename",' +
            '"playerId":' + $scope.playerId + ',' +
            '"newName":"' + $scope.renameField + '"}');
    };

    $scope.changeColor = function() {
        let packet = {};
        packet.type = 'changeColor';
        packet.playerId = $scope.playerId;
        packet.color = (getCurrentPlayer().color === 'WHITE' ? 'BLACK' : 'WHITE');
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