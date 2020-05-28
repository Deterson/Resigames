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
    $scope.playerColor = null;
    $scope.state = "setup";

    $scope.isReady = false;

    $scope.words = ["", "", "", ""];


    /* Set the width of the sidebar to 250px and the left margin of the page content to 250px */
    $scope.openNav = function() {
        document.getElementById("mySidebar").style.width = "500px";
    };

    /* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
    $scope.closeNav = function() {
        document.getElementById("mySidebar").style.width = "0";
    };

    $scope.testToasts = function() {
        $('#toast-1').toast({delay: 3000}).toast('show');
        $('#toast-2').toast({delay: 4500}).toast('show');
        $('#toast-3').toast({autohide: false}).toast('show');
    };

    // opens sidebar at begining
    $scope.openNav();


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

    function resetNumbers() {
        $scope.guessesNumbers = [[1, 2, 3, 4], [1, 2, 3, 4], [1, 2, 3, 4]];
    }

    function resetClueLists() {
        $scope.whiteClueList = [];
        $scope.blackClueList = [];
    }

    resetNumbers();
    resetCodes();
    resetClues();
    resetGuesses();
    resetClueLists();

    $scope.changeGuess = function()
    {
        recalculGuessNumbers();
    };

    function showWhiteCode()
    {
        alert("le code des blancs était " + $scope.whiteCode);
    }

    function showBlackCode()
    {
        alert("le code des noirs était " + $scope.blackCode);
    }

    // used in ng-change, constantly removes already selected numbers to avoid duplicates in guesses
    // (when a player selects "1" as their #1 clue guess, they won't be able to select "1" in other guesses)
    // alright so this one seems complicated because you'd think "oOh bUt wAit onLy oNe ArraY suFfice"
    // but NO you need to have a number array for each guess
    // and I debbuged this for HOURS, because if you choose, say, "1" and you remove it from numbers
    // so that it's like "[2, 3, 4]" well the ng-options in the jsp FALLS APPART AND NOTHING WORKS NO MORE BECAUSE
    // IT'S TRYING TO SELECT SOMETHING THAT IS NOT IN ITS ng-options ANYMORE HELP ME IT'S 3AM
    function recalculGuessNumbers()
    {
        resetNumbers();

        // TODO au moins refactoriser mdr

        if ($scope.guesses[0] !== undefined)
        {
            $scope.guessesNumbers[1].splice($scope.guessesNumbers[1].indexOf($scope.guesses[0]), 1);
            $scope.guessesNumbers[2].splice($scope.guessesNumbers[2].indexOf($scope.guesses[0]), 1);
        }

        if ($scope.guesses[1] !== undefined)
        {
            $scope.guessesNumbers[0].splice($scope.guessesNumbers[0].indexOf($scope.guesses[1]), 1);
            $scope.guessesNumbers[2].splice($scope.guessesNumbers[2].indexOf($scope.guesses[1]), 1);
        }

        if ($scope.guesses[2] !== undefined)
        {
            $scope.guessesNumbers[0].splice($scope.guessesNumbers[0].indexOf($scope.guesses[2]), 1);
            $scope.guessesNumbers[1].splice($scope.guessesNumbers[1].indexOf($scope.guesses[2]), 1);
        }
    }



    $scope.renameField = "";

    let socket = null;


    function handleYourPlayerId(packet) {
        $scope.playerId = packet.id;
        refreshPlayerColor();
    }

    function findPlayerFromId(id) {
        return $scope.game.players.find(p => p.id === id);
    }

    function refreshPlayerColor() {
        let player = getClientPlayer();
        if (player !== undefined && player !== null)
            $scope.playerColor = player.color;
    }

    function refreshScore()
    {
        if ($scope.playerColor === "WHITE") {
            $scope.yourInterceptions = $scope.game.score.whiteInterception;
            $scope.yourMalentendus = $scope.game.score.whiteMisguess;
            $scope.theirInterceptions = $scope.game.score.blackInterception;
            $scope.theirMalentendus = $scope.game.score.blackMisguess;
        }
        else if ($scope.playerColor === "BLACK") {
            $scope.yourInterceptions = $scope.game.score.blackInterception;
            $scope.yourMalentendus = $scope.game.score.blackMisguess;
            $scope.theirInterceptions = $scope.game.score.whiteInterception;
            $scope.theirMalentendus = $scope.game.score.whiteMisguess;
        }
    }

    function refreshClueLists()
    {
        resetClueLists();

        refreshWhiteClueLists();
        refreshBlackClueLists();
    }

    function refreshWhiteClueLists() {
        let maxSize = 0;
        $scope.game.whiteSheet.clueLists.forEach((cl) => {
            if (cl.clues.length > maxSize)
                maxSize = cl.clues.length;
        });

        for (let i = 0; i < maxSize; i++)
            $scope.whiteClueList.push([[], [], [], []]);

        for (let i = 0; i < maxSize; i++) {
            for (let j = 0; j < 4; j++) {
                let mot = $scope.game.whiteSheet.clueLists[j].clues[i];
                if (mot !== undefined)
                    $scope.whiteClueList[i][j] = mot;
            }
        }
    }

    function refreshBlackClueLists()
    {
        let maxSize = 0;
        $scope.game.blackSheet.clueLists.forEach((cl) => {
            if (cl.clues.length > maxSize)
                maxSize = cl.clues.length;
        });

        for (let i = 0; i < maxSize; i++)
            $scope.blackClueList.push([[], [], [], []]);

        for (let i = 0; i < maxSize; i++) {
            for (let j = 0; j < 4; j++) {
                let mot = $scope.game.blackSheet.clueLists[j].clues[i];
                if (mot !== undefined)
                    $scope.blackClueList[i][j] = mot;
            }
        }
    }

    function getClientPlayer() {
        return findPlayerFromId($scope.playerId);
    }

    function handleChangeColor(packet) {
        findPlayerFromId(packet.player.id).color = packet.color;
        refreshPlayerColor();
    }

    function handleUpdate(game) {
        // checks when steps change, and do things accordingly
        if ($scope.game.step === SETUP && game.step !== SETUP)
            $scope.closeNav();
        if ($scope.game.step === WHITEGUESS && game.step === BLACKGUESS)
            showWhiteCode();
        if ($scope.game.step === BLACKGUESS && game.step !== BLACKGUESS)
            showBlackCode();

        $scope.game = game;
        changeState();
        refreshPlayerColor();
        refreshScore();
        refreshClueLists();
    }

    function handleWordsReceive(words) {
        if (words !== undefined && words !== null)
            $scope.words = words;
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
        };

        socket.onclose = function() {
            console.log("nonon closed");
        };

        socket.onmessage = function(message) {
            let packet = JSON.parse(message.data);
            console.log(packet);
            switch (packet.type) {
                case 'yourPlayerId':
                    handleYourPlayerId(packet);
                    break;
                case 'rename':
                    findPlayerFromId(packet.player.id).name = packet.newName;
                    break;
                case 'changeColor':
                    handleChangeColor(packet);
                    break;
                case 'update':
                    handleUpdate(packet.game);
                    break;
                case 'words':
                    handleWordsReceive(packet.words);
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