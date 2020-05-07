package decrypto.action;

public class ActionRename extends Action
{
    private String newName;
    private int playerId;

    public ActionRename(String newName, int playerId)
    {
        super("rename");
        this.newName = newName;
        this.playerId = playerId;
    }

    public String getNewName()
    {
        return newName;
    }

    public void setNewName(String newName)
    {
        this.newName = newName;
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
