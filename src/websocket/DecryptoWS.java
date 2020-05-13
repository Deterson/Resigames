package websocket;

import com.google.gson.Gson;
import decrypto.Color;
import decrypto.Game;
import decrypto.Player;
import decrypto.Step;
import decrypto.action.*;
import org.codehaus.jackson.map.ObjectMapper;

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

/* TODO remettre (et trouver comment avoir un debug switch genre if (debug))

        // find player of same requestSession (i e multiple tabs opened idk)
        for(Player p : game.getPlayers())
            if (p.getRequestSession().equals(requestSession))
                player = p;
*/

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

        sendPlayerId(session);


        System.out.println("Open : player with rs " + player.getRequestSession() + " opened wsSession " + session.getId());
        System.out.println("player now has " + player.getWsSessions().size() + " wsSessions");
        System.out.println("nplayers: " + game.getPlayers().size());



    }

    private void sendPlayerId(Session session)
    {
        try {
            session.getBasicRemote().sendText("{\"type\":\"yourPlayerId\",\"id\":" + player.getId() + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void broadcastUpdateWhiteClues()
    {
        broadcast(DecryptoParser.gameUpdateWithoutWhiteClues(game));
    }


    private void broadcastUpdate()
    {
        broadcast(DecryptoParser.gameUpdate(game));
    }

    @OnMessage
    public void onTextMessage(String message)
    {
        System.out.println("received:\n" + message);
        Action action = (Action)getClassFromJson(message, Action.class); // TODO enlever (Action) à chaque fois c relou
        System.out.println(action.getType());

        switch (action.getType())
        {
            case "changeColor":
                ActionChangeColor actionChangeColor = (ActionChangeColor)getClassFromJson(message, ActionChangeColor.class);
                //do action
                game.changePlayerColor(actionChangeColor);
                //tell clients
                broadcastChangeColor(actionChangeColor);
                break;
            case "rename":
                ActionRename actionRename = (ActionRename)getClassFromJson(message, ActionRename.class);
                //do action
                game.renamePlayer(actionRename);
                //tell clients
                broadcastRename(actionRename);
                break;

            case "start":
                //do action
                game.start();
                //tell clients
                broadcastUpdate();
                break;

            case "clues":
                ActionClues actionClues = (ActionClues)getClassFromJson(message, ActionClues.class);
                // check action
                if (!game.checkActionClues(actionClues))
                    return;
                //do action
                boolean next = game.addClues(actionClues);
                //tell clients
                if (next)
                    broadcastUpdateWhiteClues();
                break;
            default:
                System.err.println("received unhandled packet");
                break;

            case "guess":
                ActionGuess actionGuess = (ActionGuess)getClassFromJson(message, ActionGuess.class);
                //check action
                if (!game.checkActionGuess(actionGuess))
                    return;
                //do action
                game.applyGuess(actionGuess);
                //tell clients
                if (game.getStep() == Step.WHITEGUESS) // still hide black clues
                    broadcastUpdateWhiteClues();
                else
                    broadcastUpdate();
                break;
        }
    }

    private Object getClassFromJson(String message, Class<? extends Action> c)
    {
        Gson g = new Gson();
        Action action = g.fromJson(message, c);
        action.setPlayer(player);
        return action;
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

    private void broadcastChangeColor(ActionChangeColor actionChangeColor)
    {
        try {
            broadcast(new ObjectMapper().writeValueAsString(actionChangeColor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void broadcastRename(ActionRename actionRename)
    {
        try {
            broadcast(new ObjectMapper().writeValueAsString(actionRename));
        } catch (IOException e) {
            e.printStackTrace();
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
