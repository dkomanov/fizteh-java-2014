package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public abstract class Command {

    private static void checkNumberOfArguments(int x, int y) throws Exception {
        if (x != y) {
            throw new Exception("Incorrect number of arguments: " + String.valueOf(x));
        }
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("Empty command");
        }
        String[] work = s.replaceFirst(" *", "").split("\\s+");
        switch (work[0]) {
            case "drop":
                checkNumberOfArguments(1, work.length - 1);
                return new DropCommand(work[1]);
            case "show":
                checkNumberOfArguments(0, work.length - 2);
                if (!work[1].equals("tables")) {
                    throw new Exception(s + ": It is unknown command");
                }
                return new ShowTablesCommand();
            case "create":
                checkNumberOfArguments(1, work.length - 1);
                return new CreateCommand(work[1]);
            case "use":
                checkNumberOfArguments(1, work.length - 1);
                return new UseCommand(work[1]);
            case "put":
                checkNumberOfArguments(2, work.length - 1);
                return new MultiPutCommand(work[1], work[2]);
            case "get":
                checkNumberOfArguments(1, work.length - 1);
                return new MultiGetCommand(work[1]);
            case "list":
                checkNumberOfArguments(0, work.length - 1);
                return new MultiListCommand();
            case "remove":
                checkNumberOfArguments(1, work.length - 1);
                return new MultiRemoveCommand(work[1]);
            case "exit":
                checkNumberOfArguments(0, work.length - 1);
                return new ExitCommand();
            default:
                throw new Exception(work[0] + ": It is unknown command");
        }
    }

    public abstract void execute(DataBaseOneDir base) throws Exception;
}
