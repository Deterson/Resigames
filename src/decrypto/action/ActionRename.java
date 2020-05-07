package decrypto.action;

public class ActionRename extends ActionPlayer
{
    private String newName;

    public ActionRename(String newName, int playerId)
    {
        super("rename", playerId);
        this.newName = newName;
    }

    public String getNewName()
    {
        return newName;
    }

    public void setNewName(String newName)
    {
        this.newName = newName;
    }


}
