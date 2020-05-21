package decrypto.sheet;

import java.util.ArrayList;
import java.util.List;

public class ClueList
{
    private List<String> clues;

    public ClueList()
    {
        clues = new ArrayList<>();
    }

    public List<String> getClues()
    {
        return clues;
    }

    public void setClues(List<String> clues)
    {
        this.clues = clues;
    }
}
