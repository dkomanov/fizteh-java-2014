public abstract class Command
{
    String name;
    String[] args;
    Command(String[] cmd)
    {
        name = cmd[0];
        args = cmd;
    }
    public static Command create(String[] cmd) throws Exception
    {
        switch(cmd[0])
        {
            case "cd": return new CdCommand(cmd);
            case "mkdir": return new MkdirCommand(cmd);
            case "pwd": return new PwdCommand(cmd);
            case "rm": return new RmCommand(cmd);
            case "cp": return new CpCommand(cmd);
            case "mv": return new MvCommand(cmd);
            case "ls": return new LsCommand(cmd);
            case "exit": return new ExitCommand(cmd);
            case "cat": return new CatCommand(cmd);
            default:
                throw new Exception("Bad command");
        }
    }
    public abstract void run() throws Exception;
}


