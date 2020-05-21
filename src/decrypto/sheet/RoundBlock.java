package decrypto.sheet;

import decrypto.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundBlock
{
    private List<String> clues;
    private Map<Color, List<Integer>> guesses; // each color got a different guess
    private List<Integer> code;

    public RoundBlock()
    {
        clues = new ArrayList<>();
        guesses = new HashMap<>();
        code = new ArrayList<>();
    }

    public List<String> getClues()
    {
        return clues;
    }

    public void setClues(List<String> clues)
    {
        this.clues = clues;
    }

    public Map<Color, List<Integer>> getGuesses()
    {
        return guesses;
    }

    public void setGuesses(List<Integer> whiteGuesses, List<Integer> blackGuesses)
    {
        addGuesses(Color.WHITE, whiteGuesses);
        addGuesses(Color.BLACK, blackGuesses);
    }

    public void addGuesses(Color color, List<Integer> guesses)
    {
        this.guesses.put(color, guesses);
    }

    public List<Integer> getCode()
    {
        return code;
    }

    public void setCode(List<Integer> code)
    {
        this.code = code;
    }
}
