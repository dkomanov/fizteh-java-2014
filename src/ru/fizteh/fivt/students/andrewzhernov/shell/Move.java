package ru.fizteh.fivt.students.andrewzhernov.shell;

public class Move {
    public static void execute(String[] args) throws Exception {
        if (args.length != 3) {
            throw new Exception("Usage: mv <source> <destination>");
        } else {
            try {
                String[] copyArgs = new String[4];
                copyArgs[0] = args[0];
                copyArgs[1] = "-r";
                copyArgs[2] = args[1];
                copyArgs[3] = args[2];
                Copy.execute(copyArgs);

                String[] removeArgs = new String[3];
                removeArgs[0] = args[0];
                removeArgs[1] = "-r";
                removeArgs[2] = args[1];
                Remove.execute(removeArgs);
            } catch (Exception e) {
                String newException = e.getMessage();
                newException = newException.replaceFirst("(cp|rm):", "mv:");
                throw new Exception(newException);
            }
        }
    }
}
