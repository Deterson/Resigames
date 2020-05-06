package websocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/websocket/decrypto")
public class DecryptoWS
{
    private static final AtomicInteger playerIds = new AtomicInteger(0);
    private final int id;

    public DecryptoWS()
    {
        this.id = playerIds.getAndIncrement();
        System.out.println("DecryptoWS created with id : " + id);
    }

    @OnOpen
    public void onOpen(Session session)
    {
        System.out.println("opened session : " + session.getBasicRemote());
    }

    @OnMessage
    public void onTextMessage(String message)
    {
        System.out.println("id " + id + " received message : "  + message);
    }

    @OnClose
    public void onClose()
    {
        System.out.println("closed id : " + id);
    }
}
