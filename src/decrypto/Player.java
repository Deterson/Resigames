package decrypto;


import decrypto.Color;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@JsonIgnoreProperties({"wsSessions playerIds"})
public class Player
{
    private static AtomicInteger playerIds = new AtomicInteger(1);
    private final int id;
    private Color color;
    private String name;
    private final String requestSession;
    private boolean ready;

    private List<Session> wsSessions;

    public Player(String requestSesion, Color color)
    {
        this.id = playerIds.getAndIncrement();
        this.color = color;
        this.requestSession = requestSesion;
        this.name = "player" + this.id;
        this.ready = false;
        wsSessions = new ArrayList<>();
    }

    public int getId()
    {
        return id;
    }

    public String getRequestSession()
    {
        return requestSession;
    }

    @JsonIgnore
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

    public boolean isReady()
    {
        return ready;
    }

    public void setReady(boolean ready)
    {
        this.ready = ready;
    }
}
