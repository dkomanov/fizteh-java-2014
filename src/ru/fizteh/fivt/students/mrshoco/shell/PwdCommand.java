public class PwdCommand extends Command
{
    PwdCommand(String[] cmd)
    {
        super(cmd);
    }
    public void run()
    {
        System.out.println(System.getProperty("user.dir"));
    }
}
