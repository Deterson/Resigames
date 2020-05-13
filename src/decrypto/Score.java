package decrypto;

public class Score
{ // TODO LA POURQUOI JSON PRENDS PAS LES ints
    private int round;

    private int whiteMisgues;
    private int blackMisguess;
    private int whiteInterception;
    private int blackInterception;

    public Score()
    {
        round = whiteInterception = whiteMisgues = blackInterception = blackMisguess = 0;
    }

    public boolean add(Token token, Color color) // returns true if game is over
    {
        if (token == Token.MISGUESS)
        {
            if (color == Color.WHITE)
                whiteMisgues++;
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

        return isGameOver();
    }

    public boolean isGameOver()
    {
        return whiteInterception >= 2
                || whiteMisgues >= 2
                || blackInterception >= 2
                || blackMisguess >= 2;
    }

    public Color whoWon() // TODO round 8
    {
        if (whiteInterception >= 2 || blackMisguess >= 2)
            return Color.WHITE;
        if (blackInterception >= 2 || whiteMisgues >= 2)
            return Color.BLACK;
        return null;
    }

    public void nextRound()
    {
        round++;
    }

    public int getRound()
    {
        return round;
    }
}
