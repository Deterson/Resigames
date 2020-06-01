package decrypto;

import decrypto.action.*;
import decrypto.sheet.Sheet;
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
    private Player modo;


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

    private Sheet whiteSheet;
    private Sheet blackSheet;

    private String whiteName;
    private String blackName;

    public Game(String path)
    {
        modo = null;
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

        whiteSheet = new Sheet();
        blackSheet = new Sheet();

        whiteName = "White Team";
        blackName = "Black Team";
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

    public void renameTeam(ActionRenameTeam actionRenameTeam)
    {
        if (actionRenameTeam.getPlayer().getColor() == Color.WHITE)
            whiteName = actionRenameTeam.getNewName();
        else
            blackName = actionRenameTeam.getNewName();
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
        if (step != Step.SETUP)
            return false;
        if (getColored(Color.BLACK).size() < 2 || getColored(Color.WHITE).size() < 2)
            return false;
        fillRandomWords();
        nextRound();
        paused = false;
        return true;
    }

    // returns 0 if nothing happened, 1 if one side filled their clues, 2 if both
    public int addClues(ActionClues actionClues)
    {
        int ret = 0;
        if (actionClues.getPlayer().getColor() == Color.WHITE)
        {
            if (whiteClues == null) // prevents from re-sending clues after chrono
            {
                whiteClues = actionClues.getClues();
                ret = 1;
            }
        }
        else
        {
            if (blackClues == null)
            {
                blackClues = actionClues.getClues();
                ret = 1;
            }
        }
        if (whiteClues != null && blackClues != null) // changes step
        {
            goToWhiteGuess();
            return 2;
        }
        return ret;
    }

    // avoids null pointer exception in some cases
    public void fillEmptyClues()
    {
        if (whiteClues == null)
        {
            whiteClues = new ArrayList<>();
            whiteClues.addAll(Arrays.asList("", "",""));
        }
        if (blackClues == null)
        {
            blackClues = new ArrayList<>();
            blackClues.addAll(Arrays.asList("", "", ""));
        }
    }

    public void goToWhiteGuess()
    {
        fillEmptyClues();
        step = Step.WHITEGUESS;
        whiteSheet.addRoundClues(whiteClues); // only show white clues on sheet
    }

    private void goToBlackGuess()
    {
        step = Step.BLACKGUESS;
        blackSheet.addRoundClues(blackClues);
        emptyGuesses();
    }

    private void nextRound()
    {
        step = Step.CLUEWRITING;
        findNextCluers();
        fillRandomCodes();
        DecryptoBroadcast.sendCodesToCluers(this);
        emptyGuesses();
        emptyClues();
        emptyReadys();
        whiteSheet.nextRound();
        blackSheet.nextRound();
        score.nextRound();
    }

    private void emptyReadys()
    {
        for (Player p : players)
            p.setReady(false);
    }

    // changes "won" attribute if any winner
    // returns if end of guess
    public boolean applyGuess(ActionGuess actionGuess)
    {
        if (actionGuess.getPlayer().getColor() == Color.WHITE)
            whiteGuess = actionGuess.getGuesses();
        else
            blackGuess = actionGuess.getGuesses();

        if (whiteGuess != null && blackGuess != null) // end of guess
        {
            applyGuessesToScore();

            if (step == Step.BLACKGUESS) // end of round
            {
                blackSheet.addRoundGuesses(whiteGuess, blackGuess);
                blackSheet.addRoundCode(blackCode);
                blackSheet.transcriptRoundOnClueList();

                if (score.isGameOver()) // GAME OVER
                {
                    step = Step.END;
                    won = score.whoWon();
                    return true;
                }
                step = Step.ENDROUND;
            }
            else
            {
                whiteSheet.addRoundGuesses(whiteGuess, blackGuess);
                whiteSheet.addRoundCode(whiteCode);
                whiteSheet.transcriptRoundOnClueList();
                goToBlackGuess();
            }
            return true;
        }
        return false;
    }

    public void applyGuessesToScore()
    {
        if (step == Step.WHITEGUESS)
        {
            applyDecrypto(whiteGuess, whiteCode, Color.WHITE);
            applyInterception(blackGuess, whiteCode, Color.BLACK);
        }
        else
        {
            applyDecrypto(blackGuess, blackCode, Color.BLACK);
            applyInterception(whiteGuess, blackCode, Color.WHITE);
        }
    }

    private void applyInterception(List<Integer> guess, List<Integer> code, Color whoIntercepts)
    {
        if (guess.equals(code))
            score.add(Token.INTERCEPTION, whoIntercepts);
    }

    private void applyDecrypto(List<Integer> guess, List<Integer> code, Color whoDecrypts)
    {
        if (!guess.equals(code))
            score.add(Token.MISGUESS, whoDecrypts);
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

    public Player getModo()
    {
        return modo;
    }

    public void changeModo()
    {
        if (players.size() == 1)
            modo = players.get(0);
        else if (getAllWsSessions().isEmpty())
            modo = null;
        else
        {
            Player oldModo = modo;
            for (Player p : players)
                if (p != oldModo && !p.getWsSessions().isEmpty())
                    modo = p;
        }
    }

    public void addPlayer(Player player)
    {
        players.add(player);
        if (players.size() == 1 || modo == null)
            modo = player;
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

    public Sheet getWhiteSheet()
    {
        return whiteSheet;
    }

    public void setWhiteSheet(Sheet whiteSheet)
    {
        this.whiteSheet = whiteSheet;
    }

    public Sheet getBlackSheet()
    {
        return blackSheet;
    }

    public void setBlackSheet(Sheet blackSheet)
    {
        this.blackSheet = blackSheet;
    }

    public String getWhiteName()
    {
        return whiteName;
    }

    public void setWhiteName(String whiteName)
    {
        this.whiteName = whiteName;
    }

    public String getBlackName()
    {
        return blackName;
    }

    public void setBlackName(String blackName)
    {
        this.blackName = blackName;
    }

    public Player removePlayer(ActionRemove actionRemove)
    {
        if (actionRemove.getPlayer() != modo)
            return null;

        Player toRemove = findPlayerById(actionRemove.getId());
        if (players.remove(toRemove))
            return toRemove;
        return null;
    }
}
