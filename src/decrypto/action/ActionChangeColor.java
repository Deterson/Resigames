package decrypto.action;

import decrypto.Color;

public class ActionChangeColor extends Action
{
    private Color color;

    public ActionChangeColor(Color color)
    {
        super("changeColor");
        this.color = color;
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
