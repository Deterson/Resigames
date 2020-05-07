package decrypto.action;

import decrypto.Color;

public class ActionChangeColor extends ActionPlayer
{
    private Color color;

    public ActionChangeColor(String type, int playerId, Color color)
    {
        super("changeColor", playerId);
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
