package decrypto.sheet;

import java.util.ArrayList;
import java.util.List;

public class Sheet
{
    private boolean transcripted;
    private int roundCount; // base 0: roundCount = round - 1
    private List<RoundBlock> roundBlocks;
    private List<ClueList> clueLists;

    public Sheet() // NEED TO CALL NEXTROUND() BEFORE USING
    {
        transcripted = false;
        roundCount = -1;
        roundBlocks = new ArrayList<>();

        clueLists = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            clueLists.add(new ClueList());
    }

    public void nextRound()
    {
        transcripted = false;
        roundBlocks.add(new RoundBlock());
        roundCount++;
    }


    public RoundBlock thisRoundBlock()
    {
        return roundBlocks.get(roundCount);
    }

    public List<RoundBlock> getRoundBlocks()
    {
        return roundBlocks;
    }

    public void setRoundBlocks(List<RoundBlock> roundBlocks)
    {
        this.roundBlocks = roundBlocks;
    }

    public List<ClueList> getClueLists()
    {
        return clueLists;
    }

    public void setClueLists(List<ClueList> clueLists)
    {
        this.clueLists = clueLists;
    }

    public void addRoundClues(List<String> clues)
    {
        thisRoundBlock().setClues(clues);
    }

    public void addRoundGuesses(List<Integer> whiteGuess, List<Integer> blackGuess)
    {
        thisRoundBlock().setGuesses(whiteGuess, blackGuess);
    }

    public void addRoundCode(List<Integer> code)
    {
        thisRoundBlock().setCode(code);
    }

    public void transcriptRoundOnClueList() // fill the clue list with the clues from the round block
    {
        if (transcripted)
            return;

        RoundBlock rb = thisRoundBlock();
        int i = 0; // index of clues

        for (Integer codeDigit : rb.getCode())
        {
            clueLists.get(codeDigit - 1).getClues().add(rb.getClues().get(i));
            i++;
        }

        transcripted = true;
    }
}
