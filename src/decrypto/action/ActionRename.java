package decrypto.action;

public class ActionRename extends Action
{
    private String newName;

    public ActionRename(String newName)
    {
        super("rename");
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
