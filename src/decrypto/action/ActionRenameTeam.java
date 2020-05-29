package decrypto.action;

public class ActionRenameTeam extends Action
{
    private String newName;

    public ActionRenameTeam(String newName)
    {
        super("renameTeam");
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
