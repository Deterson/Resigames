package decrypto;

import websocket.DecryptoBroadcast;

import java.util.TimerTask;

public class ClueTask extends TimerTask
{
    private Game game;

    public ClueTask(Game game)
    {
        this.game = game;
    }

    @Override
    public void run()
    {
        // check if clues have already been sent
        if (game.getWhiteClues() != null && game.getBlackClues() != null)
        {
            System.out.println("timer triggered, did nothing");
            return;
        }
        System.out.println("timer triggered, changed game");

        // do thing
        game.goToWhiteGuess();
        // tell clients
        DecryptoBroadcast.broadcastUpdateWhiteClues(game);
    }
}
