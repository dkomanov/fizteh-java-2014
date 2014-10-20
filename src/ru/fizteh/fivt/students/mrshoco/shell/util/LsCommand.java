package util;

import java.io.File;

/**
 * .
 */
public class LsCommand extends Command {
    /**
     * @param cmd
     *            params
     */
    LsCommand(final String[] cmd) {
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
        File folder = new File(System.getProperty("user.dir"));
        for (File file : folder.listFiles()) {
            System.out.println(file.getName());
        }
    }
}
