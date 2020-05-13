package decrypto.action;

import decrypto.Player;

public class Action
{
    private Player player;
    private String type;

    public Action()
    {
    }

    public Action(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
