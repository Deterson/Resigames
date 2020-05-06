package websocket;

import decrypto.Color;
import decrypto.Game;
import decrypto.Player;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Random;

@ServerEndpoint(value = "/websocket/decrypto")
public class DecryptoWS
{
    private static Game game = null; // global game
    private Player player; // this instance's player
    private Session wsSession; // this ws Session

    public DecryptoWS()
    {
        System.out.println("opened DecryptoWS");
    }

    @OnOpen
    public void onOpen(Session session)
    {
        this.wsSession = session;
        String requestSession = session.getRequestParameterMap().get("requestSessionId").get(0);

        if (game == null)
            game = new Game();

        // find player of same requestSession (i e multiple tabs opened idk)
        for(Player p : game.getPlayers())
        {
            if (p.getRequestSession().equals(requestSession))
                player = p;
        }

        // link new player to game if not found
        if (player == null)
        {
            Color color = new Random().nextInt(2) % 2 == 0 ? Color.BLACK : Color.WHITE;
            player = new Player(requestSession, color);
            game.addPlayer(player);
        }

        // link window to player
        player.getWsSessions().add(session);
        //inform others of player creation
        broadcastUpdate();

        System.out.println("Open : player with rs " + player.getRequestSession() + " opened wsSession " + session.getId());
        System.out.println("player now has " + player.getWsSessions().size() + " wsSessions");
        System.out.println("nplayers: " + game.getPlayers().size());



    }

    private void broadcastUpdate()
    {
        broadcast(DecryptoParser.gameUpdate(game));
    }

    @OnMessage
    public void onTextMessage(String message)
    {

    }

    @OnClose
    public void onClose()
    {
        //remove this ws from player
        player.getWsSessions().remove(wsSession);
        System.out.println("removed " + wsSession + " from player " + player.getRequestSession());
        // TODO enlever la game si plus de ws?
        if (game.getAllWsSessions().isEmpty())
        {
            game = null;
            System.out.println("removed game, no more windows");
        }
    }

    public void broadcast(String message)
    {
        try {
        for (Player p : game.getPlayers())
            for (Session s : p.getWsSessions())
                    s.getBasicRemote().sendText(message);

        } catch (IOException e) { // TODO dans quelle situation ça se déclenche?
            e.printStackTrace();
        }
    }
}
