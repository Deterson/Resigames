package websocket;


import decrypto.action.ActionUpdate;
import decrypto.Game;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static String gameUpdateWithoutWhiteClues(Game game) // removing black clues to avoid cheating
    {
        try {
            ActionUpdate act = new ActionUpdate();
            List<String> blackClues = game.getBlackClues();
            game.setBlackClues(Arrays.asList("ptdr", "ptdr", "ptdr"));
            act.setGame(game);
            String ret = new ObjectMapper().writeValueAsString(act);
            game.setBlackClues(blackClues);
            return ret;
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
