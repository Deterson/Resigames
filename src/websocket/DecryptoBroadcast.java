package websocket;

import decrypto.Color;
import decrypto.Game;
import decrypto.Player;
import decrypto.action.*;
import org.codehaus.jackson.map.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DecryptoBroadcast
{
    public static void broadcastUpdateWhiteClues(Game game)
    {
        broadcast(game, DecryptoParser.gameUpdateWithoutWhiteClues(game));
    }

    public static void broadcastUpdate(Game game)
    {
        broadcast(game, DecryptoParser.gameUpdate(game));
    }

    public static void broadcastChangeColor(Game game, ActionChangeColor actionChangeColor)
    {
        try {
            broadcast(game, new ObjectMapper().writeValueAsString(actionChangeColor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastRename(Game game, ActionRename actionRename)
    {
        try {
            broadcast(game, new ObjectMapper().writeValueAsString(actionRename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(Game game, String message)
    {
        try {
            for (Player p : game.getPlayers())
                for (Session s : p.getWsSessions())
                    synchronized (s)
                    {
                        s.getBasicRemote().sendText(message);
                    }

        } catch (IOException e) { // TODO dans quelle situation ça se déclenche?
            e.printStackTrace();
        }
    }

    public static void broadcastColoredCode(Game game, Color color)
    {
        try
        {
            List<Integer> code;
            if (color == Color.WHITE)
                code = game.getWhiteCode();
            else
                code = game.getBlackCode();

            ActionCode actionCode = new ActionCode(code, color);

            broadcast(game, new ObjectMapper().writeValueAsString(actionCode));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendCodesToCluers(Game game)
    {
        try {
            ActionCode wActionCode = new ActionCode(game.getWhiteCode());
            ActionCode bActionCode = new ActionCode(game.getBlackCode());
            for (Session s : game.getWhiteCluer().getWsSessions())
                synchronized (s) {
                    s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(wActionCode));
                }

            for (Session s : game.getBlackCluer().getWsSessions())
                synchronized (s) {
                s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(bActionCode));
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastWords(Game game)
    {
        try
        {
            ActionWords wActionWords = new ActionWords(game.getWhiteWords());
            ActionWords bActionWords = new ActionWords(game.getBlackWords());

            for (Player p : game.getPlayers())
            {
                ActionWords toSend;
                if (p.getColor().equals(Color.WHITE)) // change toSend according to player color
                    toSend = wActionWords;
                else
                    toSend = bActionWords;
                for (Session s : p.getWsSessions())
                    synchronized (s)
                    {
                        s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(toSend));
                    }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void broadcastClueTimer(Game game, boolean started)
    {
        try {
            ActionTimer toSend = new ActionTimer(started);
            for (Player p : game.getPlayers())
            {

                for (Session s : p.getWsSessions())
                    synchronized (s)
                    {
                        s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(toSend));
                    }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // WARNING : exits WS threads when closing. Careful when using this in removed player WS
    public static void broadcastRemove(Player toRemove)
    {
        try {
            synchronized (toRemove)
            {
                ActionDisconnect toSend = new ActionDisconnect();

                // use CopyOnWriteArray to avoid modifying the session list when s.close() is called
                CopyOnWriteArrayList<Session> copiedSessions = new CopyOnWriteArrayList<>(toRemove.getWsSessions());

                for (Session s : copiedSessions)
                {
                    s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(toSend));
                    s.close();
                }
            }
        }catch (IOException e) { e.printStackTrace(); }
    }
}
