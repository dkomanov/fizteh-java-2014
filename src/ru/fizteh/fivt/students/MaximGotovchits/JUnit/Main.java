package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

public class Main {
    public static void main(final String[] args) throws Exception {
        new MakeDirs().makeDirsFunction();
        if (args.length == 0) {
            new InteractiveMode().interactiveModeFunction();
        } else {
            String commands = "";
            for (String arg : args) {
                commands = commands + arg + " ";
            }
            new BatchMode().batchModeFunction(commands);
            new Exit().exitFunction();
        }
    }
}
