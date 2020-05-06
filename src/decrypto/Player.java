package decrypto;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"wsSessions"})
public class Player
{
    private Color color;
    private String name;
    private final String requestSession;

    private List<Session> wsSessions;

    public Player(String requestSesion, Color color)
    {
        this.color = color;
        this.requestSession = requestSesion;
        this.name = requestSesion;
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

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
