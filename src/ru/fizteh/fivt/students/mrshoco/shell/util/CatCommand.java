package util;

import java.io.File;
import java.util.Scanner;

/**
 * .
 */
public class CatCommand extends Command {
    /**
     * @param cmd
     *            params
     */
    CatCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * .
     */
    @Override
    public final void run() throws Exception {
        if (args.length != 2) {
            throw new Exception("Wrong number of arguments");
        }
        File file = new File(System.getProperty("user.dir"), args[1]);
        if (file.isDirectory()) {
            throw new Exception("Is a directory");
        }

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                throw new Exception(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            throw new Exception("No such file or directory");
        }
    }
}
