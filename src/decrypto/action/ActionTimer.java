package decrypto.action;

public class ActionTimer extends Action
{
    private boolean started;

    public ActionTimer(boolean started)
    {
        super("timer");
        this.started = started;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }
}
