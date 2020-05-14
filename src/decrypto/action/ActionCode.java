package decrypto.action;

import decrypto.Color;

import java.util.List;

public class ActionCode extends Action
{
    private List<Integer> code;
    private Color color;

    public ActionCode(List<Integer> code)
    {
        super("code");
        this.code = code;
        color = null;
    }

    public ActionCode(List<Integer> code, Color color)
    {
        this(code);
        this.color = color;
    }

    public List<Integer> getCode()
    {
        return code;
    }

    public void setCode(List<Integer> code)
    {
        this.code = code;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
}
