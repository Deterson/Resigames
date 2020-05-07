let decryptoApp = angular.module('decryptoApp', []);

decryptoApp.controller('decryptoCtrl', ['$scope', function ($scope) {

    $scope.ownId = ownId;

    $scope.game = {};
    $scope.game.players = [];


    let socket = null;

    function launchGame() {
        for (let i = 0; i < 10; i++)
            setTimeout(socket.send("oui"), 1000);
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
            console.log(message.data);
            let packet = JSON.parse(message.data);
            switch (packet.type) {
                case 'renamed':
                    let foundPlayer = $scope.game.players.find(p => p.id === packet.playerId);
                    foundPlayer.name = packet.newName;
                    break;
                case 'update':
                    $scope.game = packet;
                    break;
            }

            $scope.$apply();
        };
    }

    connect();

}]);