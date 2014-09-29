import java.io.File;

public class LsCommand extends Command
{
    LsCommand(String[] cmd)
    {
        super(cmd);
    }
    public void run()
    {
        File folder = new File(System.getProperty("user.dir"));
        for(File file : folder.listFiles())
        {
            System.out.println(file.getName());
        }
    }
}
