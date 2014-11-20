package fizteh.fivt.students.MaximGotovchits.Shell;

public class Main {
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            new InteractiveMode().interactiveModeFunction();
        } else {
            String commands = "";
            for (int ind = 0; ind < args.length; ++ind) {
                commands = commands + args[ind] + " ";
            }
            new BatchMode().batchModeFunction(commands);
            new Exit().exitFunction();
        }
    }
}
