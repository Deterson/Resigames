package websocket;

import com.google.gson.Gson;
import decrypto.*;
import decrypto.action.*;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;

@ServerEndpoint(value = "/websocket/decrypto", configurator=ServletAwareConfig.class)
public class DecryptoWS
{
    private Timer clueTimer;
    private static Game game = null; // global game
    private Player player; // this instance's player
    private Session wsSession; // this ws Session

    public DecryptoWS()
    {
        System.out.println("opened DecryptoWS");
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config)
    {
        clueTimer = new Timer();
        this.wsSession = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        String requestSession = session.getRequestParameterMap().get("requestSessionId").get(0);

        if (game == null)
            game = new Game(httpSession.getServletContext().getRealPath("\\WEB-INF\\words.txt"));

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
        DecryptoBroadcast.broadcastUpdate(game);

        sendPlayerId(session);


        System.out.println("Open : player with rs " + player.getRequestSession() + " opened wsSession " + session.getId());
        System.out.println("player now has " + player.getWsSessions().size() + " wsSessions");
        System.out.println("nplayers: " + game.getPlayers().size());



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
                DecryptoBroadcast.broadcastChangeColor(game, actionChangeColor);
                break;
            case "rename":
                ActionRename actionRename = (ActionRename)getClassFromJson(message, ActionRename.class);
                //do action
                game.renamePlayer(actionRename);
                //tell clients
                DecryptoBroadcast.broadcastRename(game, actionRename);
                break;

            case "renameTeam":
                ActionRenameTeam actionRenameTeam = (ActionRenameTeam)getClassFromJson(message, ActionRenameTeam.class);
                //do action
                game.renameTeam(actionRenameTeam);
                //tell clients
                DecryptoBroadcast.broadcastUpdate(game);
            case "start":
                //do action
                boolean started = game.start();
                //tell clients
                if (started)
                    DecryptoBroadcast.broadcastUpdate(game);
                break;

            case "clues":
                ActionClues actionClues = (ActionClues)getClassFromJson(message, ActionClues.class);
                // check action
                if (!game.checkActionClues(actionClues))
                    return;
                //do action
                int next = game.addClues(actionClues);

                //tell clients
                if (next == 1)
                {
                    System.out.println("started timer");
                    // in seconds;
                    clueTimer.schedule(new ClueTask(game), 32000);
                    DecryptoBroadcast.broadcastClueTimer(game, true);
                }
                if (next == 2)
                {
                    DecryptoBroadcast.broadcastClueTimer(game, false);
                    DecryptoBroadcast.broadcastUpdateWhiteClues(game); // still hides black clues
                }
                break;
            default:
                System.err.println("received unhandled packet of type " + action.getType());
                break;

            case "guess":
                ActionGuess actionGuess = (ActionGuess)getClassFromJson(message, ActionGuess.class);
                //check action
                if (!game.checkActionGuess(actionGuess))
                    return;
                //do action
                boolean endOfGuess = game.applyGuess(actionGuess);
                //tell clients
                if (endOfGuess)
                {
                    if (game.getStep() == Step.BLACKGUESS) // reveal white code
                        DecryptoBroadcast.broadcastColoredCode(game, Color.WHITE);
                    else if (game.getStep() == Step.ENDROUND) // reveal black code
                        DecryptoBroadcast.broadcastColoredCode(game, Color.BLACK);
                    DecryptoBroadcast.broadcastUpdate(game);
                }
                else
                {
                    // TODO déclencher timer et le broadcaster
                }
                break;

            case "ready":
                ActionReady actionReady = (ActionReady)getClassFromJson(message, ActionReady.class);
                //do action
                boolean allReady = game.ready(actionReady);
                //tell clients
                if (allReady)
                    DecryptoBroadcast.broadcastUpdate(game);
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

    private void sendPlayerId(Session session)
    {
        try {
            session.getBasicRemote().sendText("{\"type\":\"yourPlayerId\",\"id\":" + player.getId() + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
