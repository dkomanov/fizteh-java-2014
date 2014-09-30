package shell_util;

import java.io.File;

/**
 * .
 */
public class CdCommand extends Command {
    /**
     * @param cmd
     */
    CdCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * .
     */
    public final void run() throws Exception {
        if (args.length == 1) {
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else {
            File folder;
            if (args[1].startsWith("/")) {
                folder = new File(args[1]);
            } else {
                folder = new File(System.getProperty("user.dir"), args[1]);
            }

            if (folder.isDirectory()) {
                System.setProperty("user.dir", folder.getAbsolutePath());
            } else {
                System.out.println("No such file or directory");
            }
        }
    }
}
