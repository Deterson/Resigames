package decrypto.action;

public class ActionPlayer extends Action
{
    private int playerId;

    public ActionPlayer(String type, int playerId)
    {
        super(type);
        this.playerId = playerId;
    }

    public int getPlayerId()
    {
        return playerId;
    }

    public void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }
}
