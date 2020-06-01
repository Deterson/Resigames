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

$scope.toasts = [];

    $scope.ownId = ownId;

    $scope.game = {};
    $scope.game.players = [];

    $scope.playerId = null;
    $scope.playerColor = null;
    $scope.state = 'setup';

    $scope.words = ['', '', '', ''];

    $scope.numbers = [1, 2, 3, 4];

    $scope.cardPngPath = "backBlack.png";

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

    function listEquality(l1, l2)
    {
        if (l1.length !== l2.length)
            return false;

        for (let i = 0; i < l1.length; i++)
            if (l1[i] !== l2[i])
                return false;

        return true;
    }

    function getLastRB(color)
    {
        if (color === 'WHITE')
            return $scope.game.whiteSheet.roundBlocks[$scope.game.whiteSheet.roundCount];
        return $scope.game.blackSheet.roundBlocks[$scope.game.blackSheet.roundCount];
    }

    // opens sidebar at begining
    $scope.openNav();


    function resetCodes()
    {
        $scope.code = null;
        $scope.whiteCode = null;
        $scope.blackCode = null;
    }

    function resetTimer()
    {
        let bar = document.getElementById("timer");
        bar.style.width = '100%';
        $scope.timerPct = 100;
    }

    function resetInputClues()
    {
        $scope.cluesSent = false;
        $scope.inputClues = ['', '', ''];
    }

    function resetClues()
    {
        $scope.clues = ['', '', ''];
    }

    function resetGuesses()
    {
        $scope.guesses = [1, 1, 1];
    }

    function resetNumbers() {
        $scope.guessesNumbers = [[1, 2, 3, 4], [1, 2, 3, 4], [1, 2, 3, 4]];
    }

    function resetClueLists() {
        $scope.whiteClueList = [];
        $scope.blackClueList = [];
    }

    function resetReady() {
        $scope.isReady = false;
    }

    function resetModal() {
        $scope.modal = {
            message:'',
            picPath: 'win.jpg',
            hasButton: false,
            buttonMessage: ''
        };
    }

    resetModal();
    resetTimer();
    resetReady();
    resetNumbers();
    resetCodes();
    resetInputClues();
    resetGuesses();
    resetClueLists();

    function resetInputs() {
        resetGuesses();
        resetReady();
    }

    function resetInputsRound() {
        resetInputs();
        resetInputClues();
    }

    $scope.isIntercepted = function(color)
    {
        if (color === 'WHITE')
        {
            let rb = getLastRB('WHITE');
            return listEquality(rb.guesses.BLACK, (rb.code));
        }
        let rb = getLastRB('BLACK');
        return listEquality(rb.guesses.WHITE, rb.code);
    };

    $scope.isMissguessed = function(color)
    {
        if (color === 'WHITE')
        {
            let rb = getLastRB('WHITE');
            return !listEquality(rb.guesses.WHITE, rb.code);
        }
        let rb = getLastRB('BLACK');
        return !listEquality(rb.guesses.BLACK, rb.code);
    };

    function showWhiteCode() {
        resetModal();

        let intercepted = $scope.isIntercepted('WHITE');
        let missguessed = $scope.isMissguessed('WHITE');
        let ourTeam = $scope.playerColor === 'WHITE';
        let code = getLastRB('WHITE').code;

        showCode(intercepted, missguessed, ourTeam, code);
    }

    function showBlackCode() {
        resetModal();

        let intercepted = $scope.isIntercepted('BLACK');
        let missguessed = $scope.isMissguessed('BLACK');
        let ourTeam = $scope.playerColor === 'BLACK';
        let code = getLastRB('BLACK').code;

        showCode(intercepted, missguessed, ourTeam, code);
    }

    function showCode(intercepted, missguessed, ourTeam, code)
    {
        let codeText = '<b>' + code[0] + '.' + code[1] + '.' + code[2] + '</b>';
        let message = 'Le code était ' + codeText + '<br/>';
        if (intercepted)
            message += ourTeam ? "L'équipe adverse l'a intercepté !"
            : "Nous l'avons intercepté !";
        message += '<br/>';

        if (missguessed)
            message += ourTeam ? "Notre équipe n'a pas réussi à le déchiffrer !"
                : "L'équipe adverse n'a pas réussi à le déchiffrer !";

        $scope.modal.message = message;
        if (intercepted)
            $scope.modal.picPath = 'interception.png';
        else if (missguessed)
            $scope.modal.picPath = 'malentendu.png';
        else if (intercepted && missguessed)
            $scope.modal.picPath = 'inter-malen.png';
        else
            $scope.modal.picPath = 'boite-decrypto.png';

        $('#modal').modal();
    }


    $scope.renameField = '';
    $scope.renameTeamField = '';

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
        if (player !== undefined && player !== null) {
            $scope.playerColor = player.color;
            refreshCodePicture();
        }
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

    function refreshClues()
    {
        if ($scope.state === 'WHITEGUESS')
            $scope.clues = $scope.game.whiteClues;
        else if ($scope.state === 'BLACKGUESS')
            $scope.clues = $scope.game.blackClues;
        else
            resetClues();
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
        console.log($scope);
        // checks when steps change, and do things accordingly
        if ($scope.game.step === SETUP && game.step !== SETUP)
            $scope.closeNav();
        if ($scope.game.step !== WHITEGUESS && game.step === WHITEGUESS)
            resetTimer();

        if ($scope.game.step === WHITEGUESS && game.step === BLACKGUESS) {
            resetInputs();
            $scope.game = game;
            showWhiteCode();
        }
        if ($scope.game.step === BLACKGUESS && game.step !== BLACKGUESS) {
            resetInputsRound();
            $scope.game = game;
            showBlackCode();
        }

        $scope.game = game;
        changeState();
        changeStateText();

        refreshClues();
        refreshCodePicture();
        refreshPlayerColor();
        refreshScore();
        refreshClueLists();
    }

    function handleWordsReceive(words) {
        if (words !== undefined && words !== null)
            $scope.words = words;
    }

    function changeStateText() {
        $scope.stateText = calculateStateText();
    }

    function refreshCodePicture() {
        if ($scope.state !== SETUP && $scope.state !== ENDROUND
            && ($scope.game.whiteCluer.id === $scope.playerId || $scope.game.blackCluer.id === $scope.playerId))
        {
            if ($scope.playerColor === 'WHITE')
                $scope.cardPngPath = 'blankWhite.png';
            else
                $scope.cardPngPath = 'blankBlack.png';
        }
        else
        {
            if ($scope.playerColor === 'WHITE')
                $scope.cardPngPath = 'backWhite.png';
            else
                $scope.cardPngPath = 'backBlack.png';
        }
    }

    function calculateStateText()
    {
        switch ($scope.state)
        {
            case 'SETUP':
                return 'Attendez le lancement de la partie';
            case 'CLUEWRITING':
                return 'Rédigez vos indices pour le code donné';
            case 'WHITEGUESS':
            case 'BLACKGUESS':
                if (($scope.state === 'WHITEGUESS' && $scope.playerColor === 'WHITE')
                    || ($scope.state === 'BLACKGUESS' && $scope.playerColor === 'BLACK'))
                    return 'Déchiffrez le code de votre coéquipier grâce à ses indices';
                return 'Interceptez le code de l\'adversaire. Aidez-vous des indices précédents';
            case 'WAIT':
                return 'Attendez que les autres joueurs décryptent votre code';
            case 'ENDROUND':
                return 'cliquez sur le bouton lorsque vous êtes prêt';
            case 'END':
                return 'la partie est terminée, les ' + game.won + ' ont gagné';
        }
    }

    function changeState()
    {
        switch ($scope.game.step) {
            case SETUP :
                $scope.state = 'SETUP';
                break;
            case CLUEWRITING:
                if ($scope.game.whiteCluer.id === $scope.playerId
                    || $scope.game.blackCluer.id === $scope.playerId)
                    $scope.state = 'CLUEWRITING';
                else
                    $scope.state = 'WAIT';
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
                $scope.state = 'ENDROUND';
                break;

            case END:
                $scope.state = 'END';
        }

    }

    function triggerTimer(){
        $scope.timerPct = 100;
        decreaseTimer();
    }
    // 32 seconds on server side but 31.5s here, so that if client is still witing clues it has time to send it
    const delayTimer = 31500 / 100;

    function decreaseTimer(){
        $scope.timerPct--;
        document.getElementById('timer').style.width = $scope.timerPct + '%';
        if ($scope.timerPct === 0)
            $scope.sendClues();
        else if ($scope.state === CLUEWRITING)
            setTimeout(decreaseTimer, delayTimer);
    }

    function connect()
    {
        let preurl = window.location.protocol === 'http:' ? 'ws://' : 'wss://';
        let url = preurl + window.location.host + '/websocket/decrypto?requestSessionId=' + ownId;

        if ('WebSocket' in window)
            socket = new WebSocket(url);
        else
            alert('Error: WebSocket is not supported by this browser.');

        socket.onopen = function() {
            console.log('oui opené');
        };

        socket.onclose = function() {
            console.log('nonon closed');
        };

        socket.onmessage = function(message) {
            let packet = JSON.parse(message.data);
            console.log(packet);
            switch (packet.type) {
                case 'yourPlayerId':
                    handleYourPlayerId(packet);
                    break;
                case 'disconnect':
                    alert("You've been disconnected");
                    $scope.game = null;
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
                case 'timer':
                    if (packet.started)
                        triggerTimer();
                    break;
                case 'code':
                    console.log('code!');
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

    $scope.renameTeam = function() {
        console.log($scope.renameTeamField);
        if (!isAlphaNumerical($scope.renameTeamField))
            return;
        let packet = {};
        packet.type = 'renameTeam';
        packet.newName = $scope.renameTeamField;
        socket.send(JSON.stringify(packet));
    };


    $scope.removePlayer = function(id)
    {
        let packet = {};
        packet.type = 'remove';
        packet.id = id;
        socket.send(JSON.stringify(packet));
    };

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
        $scope.cluesSent = true;
        let packet = {};
        packet.type = 'clues';
        packet.clues = $scope.inputClues;
        socket.send(JSON.stringify(packet));
    };

    $scope.sendGuesses = function()
    {
        let packet = {};
        packet.type = 'guess';
        packet.guesses = $scope.guesses;
        socket.send(JSON.stringify(packet));
    };

    $scope.sendReady = function(ready)
    {
        $scope.isReady = !$scope.isReady;
        let packet = {};
        packet.type = 'ready';
        packet.ready = ready;
        socket.send(JSON.stringify(packet));
    };

    function isAlphaNumerical(input)
    {
        if (/^[a-z0-9]+$/i.test(input))
            return true;
        if (input === undefined || input === null || input === '')
            alert('valeur non vide pls');
        else
            alert('only alphanumerical characters (ouais pélo c\'est pas pour tout de suite l\'injection SQL)');
        return false;
    }

    connect();

}]);