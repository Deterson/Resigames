package decrypto.action;

import decrypto.Game;

public class ActionUpdate extends Action
{
    private Game game;

    public ActionUpdate()
    {
        super("update");
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }
}
