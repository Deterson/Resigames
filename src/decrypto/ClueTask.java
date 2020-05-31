package decrypto;

import websocket.DecryptoBroadcast;

import java.util.TimerTask;

public class ClueTask extends TimerTask
{
    private Game game;
    private int round;

    public ClueTask(Game game)
    {
        this.game = game;
        this.round = game.getScore().getRound();
    }

    @Override
    public void run()
    {
        // check if clues have already been sent, or if not in the same round
        if (game.getWhiteClues() != null && game.getBlackClues() != null)
        {
            System.out.println("timer triggered, did nothing : both clues have been written");
            return;
        }

        if (game.getStep() != Step.CLUEWRITING || game.getScore().getRound() != round)
        {
            System.out.println("timer triggered, did nothing : not in the same step or round");
            return;
        }
        System.out.println("timer triggered, changed game");

        // do thing
        game.goToWhiteGuess();
        // tell clients
        DecryptoBroadcast.broadcastUpdateWhiteClues(game);
    }
}
