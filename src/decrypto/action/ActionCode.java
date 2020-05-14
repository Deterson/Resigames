package decrypto.action;

import java.util.List;

public class ActionCode extends Action
{
    private List<Integer> code;

    public ActionCode(List<Integer> code)
    {
        super("code");
        this.code = code;
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
