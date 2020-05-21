package decrypto;

public class Score
{
    private int round;

    private int whiteMisguess;
    private int blackMisguess;
    private int whiteInterception;
    private int blackInterception;

    public Score()
    {
        round = whiteInterception = whiteMisguess = blackInterception = blackMisguess = 0;
    }

    public void add(Token token, Color color) // returns true if game is over
    {
        if (token == Token.MISGUESS)
        {
            if (color == Color.WHITE)
                whiteMisguess++;
            else
                blackMisguess++;
        }
        else
        {
            if (color == Color.WHITE)
                whiteInterception++;
            else
                blackInterception++;
        }
    }

    public boolean isGameOver()
    {
        return whiteInterception >= 2
                || whiteMisguess >= 2
                || blackInterception >= 2
                || blackMisguess >= 2;
    }

    public Winner whoWon() // TODO round 8
    {
        if ((whiteInterception == 2 && blackInterception == 2) || (whiteMisguess == 2 && blackMisguess == 2))
            return Winner.DRAW;
        if (whiteInterception == 2 || blackMisguess == 2)
            return Winner.WHITE;
        if (blackInterception == 2 || whiteMisguess == 2)
            return Winner.BLACK;
        return Winner.NONE;
    }

    public void nextRound()
    {
        round++;
    }

    public int getRound()
    {
        return round;
    }

    public int getWhiteMisguess()
    {
        return whiteMisguess;
    }

    public void setWhiteMisguess(int whiteMisguess)
    {
        this.whiteMisguess = whiteMisguess;
    }

    public int getBlackMisguess()
    {
        return blackMisguess;
    }

    public void setBlackMisguess(int blackMisguess)
    {
        this.blackMisguess = blackMisguess;
    }

    public int getWhiteInterception()
    {
        return whiteInterception;
    }

    public void setWhiteInterception(int whiteInterception)
    {
        this.whiteInterception = whiteInterception;
    }

    public int getBlackInterception()
    {
        return blackInterception;
    }

    public void setBlackInterception(int blackInterception)
    {
        this.blackInterception = blackInterception;
    }
}
