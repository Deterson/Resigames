package decrypto.action;

import java.util.List;

public class ActionGuess extends Action
{
    List<Integer> guesses;

    public List<Integer> getGuesses()
    {
        return guesses;
    }

    public void setGuesses(List<Integer> guesses)
    {
        this.guesses = guesses;
    }

    public boolean check()
    {
        return guesses != null && guesses.size() == 3
                && !guesses.get(0).equals(guesses.get(1))
                && !guesses.get(0).equals(guesses.get(2))
                && !guesses.get(1).equals(guesses.get(2));
    }
}
