import java.io.File;

/**
*.
*/
public class LsCommand extends Command {
    /**
    * @param cmd params
    */
    LsCommand(final String[] cmd) {
        super(cmd);
    }
    /**
    *.
    */
    public final void run() {
        File folder = new File(System.getProperty("user.dir"));
        for (File file : folder.listFiles()) {
            System.out.println(file.getName());
        }
    }
}
