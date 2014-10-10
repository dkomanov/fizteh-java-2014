package util;

/**
 * .
 */
public class MvCommand extends Command {
    /**
     * @param cmd
     *            params
     */
    MvCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * .
     */
    @Override
    public final void run() throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments");
            return;
        }

        try {
            new CpCommand(new String[] {"cp", "-r", args[1], args[2]}).run();
            new RmCommand(new String[] {"rm", "-r", args[1]}).run();
        } catch (Exception e) {
            throw e;
        }
    }
}
