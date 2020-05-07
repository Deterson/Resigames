package decrypto;

import decrypto.action.ActionRename;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.websocket.Session;
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

    @JsonIgnore
    public Collection<Session> getAllWsSessions()
    {
        List<Session> ret = new ArrayList<>();
        for (Player p : players)
            ret.addAll(p.getWsSessions());

        return ret;
    }

    public void renamePlayer(ActionRename actionRename)
    {
        for (Player p : players)
        {
            if (p.getId() == actionRename.getPlayerId())
            {
                p.setName(actionRename.getNewName());
                break;
            }
        }
    }
}
