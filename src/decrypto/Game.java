package decrypto;

import javax.websocket.Session;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Game
{
    private List<Player> players;

    public Game()
    {
        players = new ArrayList<>();
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    public void addPlayer(Player player)
    {
        players.add(player);
    }

    public Collection<Session> getAllWsSessions()
    {
        List<Session> ret = new ArrayList<>();
        for (Player p : players)
            ret.addAll(p.getWsSessions());

        return ret;
    }
}
