let decryptoApp = angular.module('decryptoApp', []);

decryptoApp.controller('decryptoCtrl', ['$scope', function ($scope) {
    $scope.ownId = ownId;

    let game = {};

    game.socket = null;

    function launchGame() {
        for (let i = 0; i < 10; i++)
            setTimeout(sendMessage, 1000);
    }

    function connect()
    {
        let url;
        if (window.location.protocol === 'http:') {
            url = 'ws://' + window.location.host + '/websocket/decrypto';
        } else {
            url = 'wss://' + window.location.host + '/websocket/decrypto';
        }

        if ('WebSocket' in window) {
            game.socket = new WebSocket(url);
        } else if ('MozWebSocket' in window) {
            game.socket = new MozWebSocket(url); //TODO peut-être enlever?
        } else {
            alert('Error: WebSocket is not supported by this browser.');
        }

        game.socket.onopen = function() {
            console.log("oui opené");
            launchGame();
        };

        game.socket.onclose = function() {
            console.log("nonon closed");
        };

        game.socket.onmessage = function(message) {
            console.log(message);
        };
    }

    connect();

}]);