package decrypto;

import decrypto.action.ActionChangeColor;
import decrypto.action.ActionRename;
import exception.PlayerMissingException;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.websocket.Session;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Game
{
    private List<Player> players;
    private Step step;
    private boolean paused;
    private Random r;

    private Player whiteCluer;
    private Player blackCluer;

    public Game()
    {
        r = new Random();
        players = new ArrayList<>();
        step = Step.SETUP;
        paused = true;
        whiteCluer = blackCluer = null;
    }

    @JsonIgnore
    public Collection<Session> getAllWsSessions()
    {
        List<Session> ret = new ArrayList<>();
        for (Player p : players)
            ret.addAll(p.getWsSessions());

        return ret;
    }

    public Player findPlayerById(int id)
    {
        for (Player p : players)
            if (p.getId() == id)
                return p;

        System.err.println("player doesn't exist with id : " + id);
        new PlayerMissingException().printStackTrace();
        return null;
    }

    public void renamePlayer(ActionRename actionRename)
    {
        actionRename.getPlayer().setName(actionRename.getNewName());
    }

    public void changePlayerColor(ActionChangeColor actionChangeColor)
    {
        actionChangeColor.getPlayer().setColor(actionChangeColor.getColor());
    }

    public boolean start()
    {
        if (getColored(Color.BLACK).isEmpty() || getColored(Color.WHITE).isEmpty())
            return false;
        step = Step.CLUEWRITING;
        findNextCluers();
        paused = false;
        return true;
    }

    private void findNextCluers()
    {
        if (whiteCluer == null)
            whiteCluer = findRandomColoredCluer(Color.WHITE);
        else
            whiteCluer = findNextColoredCluer(whiteCluer, Color.WHITE);
        if (blackCluer == null)
            blackCluer = findRandomColoredCluer(Color.BLACK);
        else
            blackCluer = findNextColoredCluer(blackCluer, Color.BLACK);

    }

    private Player findNextColoredCluer(Player whiteCluer, Color color)
    {
        List<Player> colored = getColored(color);
        int i = colored.indexOf(whiteCluer);
        do
        {
            i++;
            if (i >= colored.size())
                i = 0;
        } while (!colored.get(i).getColor().equals(color));
        return colored.get(i);
    }

    private Player findRandomColoredCluer(Color color)
    {
        List<Player> colored = getColored(color);
        return colored.get(r.nextInt(colored.size()));
    }


    public List<Player> getPlayers()
    {
        return players;
    }

    public List<Player> filterPlayers(Predicate<? super Player> p)
    {
        return players.stream().filter(p).collect(Collectors.toList());
    }

    public List<Player> getColored(Color color)
    {
        return filterPlayers(player -> player.getColor().equals(color));
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    public void addPlayer(Player player)
    {
        players.add(player);
    }

    public Step getStep()
    {
        return step;
    }

    public void setStep(Step step)
    {
        this.step = step;
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    public Player getWhiteCluer()
    {
        return whiteCluer;
    }

    public void setWhiteCluer(Player whiteCluer)
    {
        this.whiteCluer = whiteCluer;
    }

    public Player getBlackCluer()
    {
        return blackCluer;
    }

    public void setBlackCluer(Player blackCluer)
    {
        this.blackCluer = blackCluer;
    }
}
