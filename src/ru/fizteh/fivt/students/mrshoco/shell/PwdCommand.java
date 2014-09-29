/**
*.
*/
public class PwdCommand extends Command {
    /**
    * @param cmd params
    */
    PwdCommand(final String[] cmd) {
        super(cmd);
    }
    /**
    *.
    */
    public final void run() {
        System.out.println(System.getProperty("user.dir"));
    }
}
