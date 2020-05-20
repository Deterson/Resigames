package decrypto;

import decrypto.action.*;
import exception.PlayerMissingException;
import org.codehaus.jackson.annotate.JsonIgnore;
import websocket.DecryptoBroadcast;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game
{
    private Random r;
    private String dicoPath;

    private List<Player> players;
    private Step step;
    private boolean paused;
    private Score score;
    private Winner won;

    private Player whiteCluer;
    private List<String> whiteClues;
    private Player blackCluer;
    private List<String> blackClues;
    private List<Integer> whiteCode;
    private List<Integer> blackCode;
    private List<Integer> whiteGuess;
    private List<Integer> blackGuess;

    private List<String> whiteWords;
    private List<String> blackWords;

    public Game(String path)
    {
        dicoPath = path;
        System.out.println("path : " + path);
        r = new Random();
        players = new ArrayList<>();
        won = Winner.NONE;
        step = Step.SETUP;
        paused = true;
        score = new Score();
        whiteCluer = blackCluer = null;
        whiteCode = blackCode = null;
        emptyGuesses();
        emptyClues();
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

    private void fillRandomWords()
    {
        Supplier<Stream<String>> dicoSupplier = () -> {
            try { return Files.lines(Paths.get(dicoPath)); }
            catch (IOException e) { e.printStackTrace(); return null;}
        };

        Stream<String> dicoStreamCount = dicoSupplier.get();
        int nLines = (int)dicoStreamCount.count();

        List<Integer> indexes = new ArrayList<>();
        while (indexes.size() < 8)
        {
            int i = r.nextInt(nLines);
            if (indexes.indexOf(i) != -1)
                continue;
            indexes.add(i);
        }

        List<String> selectedWords = new ArrayList<>();
        for (int i : indexes)
        {
            Stream<String> dicoStreamGet = dicoSupplier.get();
            Optional<String> found = dicoStreamGet.skip(i).findFirst();
            String word = found.get();
            selectedWords.add(word);
        }

        whiteWords = new ArrayList<>();
        whiteWords.addAll(selectedWords.subList(0, 4));
        blackWords = new ArrayList<>();
        blackWords.addAll(selectedWords.subList(4, 8));

        DecryptoBroadcast.broadcastWords(this);
    }

    public boolean start()
    {
        if (getColored(Color.BLACK).isEmpty() || getColored(Color.WHITE).isEmpty())
            return false;
        fillRandomWords();
        nextRound();
        paused = false;
        return true;
    }

    public boolean addClues(ActionClues actionClues)
    {
        if (actionClues.getPlayer().getColor() == Color.WHITE)
            whiteClues = actionClues.getClues();
        else
            blackClues = actionClues.getClues();
        if (whiteClues != null && blackClues != null) // changes step
        {
            step = Step.WHITEGUESS;
            return true;
        }
        return false;
    }

    private void goToBlackGuess()
    {
        step = Step.BLACKGUESS;
        emptyGuesses();
    }

    private void nextRound() // TODO historique
    {
        step = Step.CLUEWRITING;
        findNextCluers();
        fillRandomCodes();
        DecryptoBroadcast.sendCodesToCluers(this);
        emptyGuesses();
        emptyClues();
        score.nextRound();
    }

    // changes "won" attribute if any winner
    // returns if end of guess
    public boolean applyGuess(ActionGuess actionGuess)
    {
        if (actionGuess.getPlayer().getColor() == Color.WHITE)
            whiteGuess = actionGuess.getGuesses();
        else
            blackGuess = actionGuess.getGuesses();

        boolean guessResult = guessResult(actionGuess);

        if (whiteGuess != null && blackGuess != null) // end of guess
        {
            if (step == Step.BLACKGUESS) // end of round
            {
                if (guessResult) // GAME OVER
                {
                    step = Step.END;
                    won = score.whoWon();
                    return true;
                }
                step = Step.ENDROUND;
            }
            else
                goToBlackGuess();
            return true;
        }
        return false;
    }


    public boolean guessResult(ActionGuess actionGuess) // TODO need a guessColor in Game attributes, coz that's ugly af
    {
        Player p = actionGuess.getPlayer();
        if (step == Step.WHITEGUESS)
        {
            if (p.getColor() == Color.WHITE)
                return applyDecryptoz(actionGuess);
            return applyInterception(actionGuess);
        }
        if (step == Step.BLACKGUESS)
        {
            if (p.getColor() == Color.BLACK)
                return applyDecryptoz(actionGuess);
            return applyInterception(actionGuess);
        }
        throw new IllegalStateException("tried to apply guess while neither in WHITEGUESS nor BLACKGUESS step");
    }

    private boolean applyInterception(ActionGuess actionGuess)
    {
        List<Integer> colorCode = getColorCode(actionGuess.getPlayer().getColor());
        if (actionGuess.getGuesses().equals(colorCode))
            return score.add(Token.INTERCEPTION, actionGuess.getPlayer().getColor());
        return false;
    }

    private boolean applyDecryptoz(ActionGuess actionGuess)
    {
        List<Integer> colorCode = getColorCode(actionGuess.getPlayer().getColor());

        if (!actionGuess.getGuesses().equals(colorCode))
            return score.add(Token.MISGUESS, actionGuess.getPlayer().getColor());
        return false;
    }


    private void fillRandomCodes()
    {
        List<Integer> codes = Arrays.asList(1, 2, 3, 4);
        blackCode = new ArrayList<>();
        whiteCode = new ArrayList<>();

        Collections.shuffle(codes, r);
        blackCode.addAll(codes.subList(0, 3));

        Collections.shuffle(codes, r);
        whiteCode.addAll(codes.subList(0, 3));
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


    private void emptyClues()
    {
        whiteClues = blackClues = null;
    }

    private void emptyGuesses()
    {
        whiteGuess = blackGuess = null;
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

    public Score getScore()
    {
        return score;
    }

    public Winner getWon()
    {
        return won;
    }

    public void setWon(Winner won)
    {
        this.won = won;
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

    public List<String> getBlackClues()
    {
        return blackClues;
    }

    public void setBlackClues(List<String> blackClues)
    {
        this.blackClues = blackClues;
    }

    public List<String> getWhiteClues()
    {
        return whiteClues;
    }

    public void setWhiteClues(List<String> whiteClues)
    {
        this.whiteClues = whiteClues;
    }

    @JsonIgnore
    public List<Integer> getWhiteCode()
    {
        return whiteCode;
    }

    public void setWhiteCode(List<Integer> whiteCode)
    {
        this.whiteCode = whiteCode;
    }

    @JsonIgnore
    public List<Integer> getBlackCode()
    {
        return blackCode;
    }

    public void setBlackCode(List<Integer> blackCode)
    {
        this.blackCode = blackCode;
    }

    @JsonIgnore
    public List<String> getWhiteWords()
    {
        return whiteWords;
    }

    @JsonIgnore
    public List<String> getBlackWords()
    {
        return blackWords;
    }

    private List<Integer> getColorCode(Color color)
    {
        if (color == Color.WHITE)
            return whiteCode;
        return blackCode;
    }

    public List<Integer> getWhiteGuess()
    {
        return whiteGuess;
    }

    public void setWhiteGuess(List<Integer> whiteGuess)
    {
        this.whiteGuess = whiteGuess;
    }

    private List<Integer> getColorGuess(Color color)
    {
        if (color == Color.WHITE)
            return whiteGuess;
        return blackGuess;
    }

    public List<Integer> getBlackGuess()
    {
        return blackGuess;
    }

    public void setBlackGuess(List<Integer> blackGuess)
    {
        this.blackGuess = blackGuess;
    }

    public boolean checkActionClues(ActionClues actionClues)
    {
        if (!actionClues.getPlayer().equals(whiteCluer) && !actionClues.getPlayer().equals(blackCluer))
            return false;
        return step == Step.CLUEWRITING && actionClues.getClues() != null && actionClues.getClues().size() == 3;
    }


    public boolean checkActionGuess(ActionGuess actionGuess) // prevents cluers from sending their own guess
    {
        if (step == Step.WHITEGUESS && actionGuess.getPlayer() == whiteCluer)
            return false;
        if (step == Step.BLACKGUESS && actionGuess.getPlayer() == blackCluer)
            return false;
        return (step == Step.WHITEGUESS || step == Step.BLACKGUESS) && actionGuess.check();
    }

    public boolean ready(ActionReady actionReady)
    {
        actionReady.getPlayer().setReady(actionReady.isReady());
        boolean ret = everyoneReady();
        if (ret)
            nextRound();
        return ret;
    }

    private boolean everyoneReady()
    {
        for (Player p : players)
            if (!p.isReady())
                return false;
        return true;
    }
}
