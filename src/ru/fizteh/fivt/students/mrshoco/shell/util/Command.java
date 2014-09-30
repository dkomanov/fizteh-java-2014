package util;

/**
 * class Command.
 */
public abstract class Command {
    /**
     *
     */
    protected String name;
    /**
     *
     */
    protected String[] args;

    /**
     *
     *
     * @param cmd
     */
    Command(final String[] cmd) {
        name = cmd[0];
        args = cmd;
    }

    /**
     *
     *
     * @param cmd
     * @return
     *
     * @throws Exception
     */
    public static Command create(final String[] cmd) throws Exception {
        switch (cmd[0]) {
        case "cd":
            return new CdCommand(cmd);
        case "mkdir":
            return new MkdirCommand(cmd);
        case "pwd":
            return new PwdCommand(cmd);
        case "rm":
            return new RmCommand(cmd);
        case "cp":
            return new CpCommand(cmd);
        case "mv":
            return new MvCommand(cmd);
        case "ls":
            return new LsCommand(cmd);
        case "exit":
            return new ExitCommand(cmd);
        case "cat":
            return new CatCommand(cmd);
        default:
            throw new Exception("Bad command");
        }
    }

    /**
     *
     *
     *
     * @throws Exception
     */
    public abstract void run() throws Exception;
}
