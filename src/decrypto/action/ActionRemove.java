package decrypto.action;

public class ActionRemove extends Action
{
    private int id;

    public ActionRemove(int id)
    {
        super("remove");
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
