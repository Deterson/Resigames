package decrypto;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

public class Player
{
    private final String requestSession;
    private List<Session> wsSessions;

    public Player(String requestSesion)
    {
        this.requestSession = requestSesion;
        wsSessions = new ArrayList<>();
    }

    public String getRequestSession()
    {
        return requestSession;
    }

    public List<Session> getWsSessions()
    {
        return wsSessions;
    }

    public void setWsSessions(List<Session> wsSessions)
    {
        this.wsSessions = wsSessions;
    }
}
