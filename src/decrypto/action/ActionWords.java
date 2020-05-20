package decrypto.action;

import java.util.List;

public class ActionWords extends Action
{
    List<String> words;

    public ActionWords(List<String> words)
    {
        super("words");
        this.words = words;
    }

    public List<String> getWords()
    {
        return words;
    }

    public void setWords(List<String> words)
    {
        this.words = words;
    }
}
