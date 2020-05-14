package decrypto.action;

public class ActionReady extends Action
{
    private boolean ready;

    public ActionReady()
    {
        this(true);
    }

    public ActionReady(boolean ready)
    {
        super("ready");
        this.ready = ready;
    }

    public boolean isReady()
    {
        return ready;
    }

    public void setReady(boolean ready)
    {
        this.ready = ready;
    }
}
