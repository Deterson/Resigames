package websocket;


import decrypto.action.ActionUpdate;
import decrypto.Game;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class DecryptoParser
{

    public static String gameUpdate(Game game)
    {
        try {
            ActionUpdate act = new ActionUpdate();
            act.setGame(game);
            return new ObjectMapper().writeValueAsString(act);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String parsePlayers(Game game)
    {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(game.getPlayers());
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        StringBuilder sb = new StringBuilder("[");
        for (Player p : game.getPlayers())
        {
            sb.append("{\"name\":\"").append(p.getName())
                    .append("\", \"color\":\"").append(p.getColor())
                    .append("\", \"id\":\"").append(p.getRequestSession()).append("\"},");
        }
        sb.deleteCharAt(sb.length() - 1); // removes last ','
        sb.append("]");

        return sb.toString();
*/
return "";
    }
}
