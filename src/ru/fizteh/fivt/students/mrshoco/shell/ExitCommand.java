import java.io.IOException;

public class ExitCommand extends Command
{
    ExitCommand(String[] cmd)
    {
        super(cmd);
    }
    public void run() throws Exception
    {
        throw new IOException("Exit");
    }
}
