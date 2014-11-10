package util;

/**
 * .
 */
public class PwdCommand extends Command {
    /**
     * @param cmd
     *            params
     */
    PwdCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * .
     */
    @Override
    public final void run() throws Exception {
        if (args.length > 1) {
            throw new Exception("Wrong number of arguments");
        }
        System.out.println(System.getProperty("user.dir"));
    }
}
