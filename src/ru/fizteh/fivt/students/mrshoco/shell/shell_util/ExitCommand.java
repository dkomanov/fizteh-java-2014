package shell_util;

import java.io.IOException;

/**
 * .
 */
public class ExitCommand extends Command {
    /**
     * @param cmd
     */
    ExitCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * .
     */
    public final void run() throws Exception {
        throw new IOException("Exit");
    }
}
