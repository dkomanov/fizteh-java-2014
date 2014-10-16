package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class Command {

    private static void checkArgumentsNumber(int expected, int actual) throws Exception {
        if (expected != actual) {
            throw new Exception("Unexpected number of arguments: " + String.valueOf(expected) + " expected");
        }
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("Empty command");
        }
        String[] tokens = s.replaceFirst(" *", "").split("\\s+");
        switch(tokens[0]) {
            case "create":
                checkArgumentsNumber(1, tokens.length - 1);
                return new CreateCommand(tokens[1]);
            case "drop":
                checkArgumentsNumber(1, tokens.length - 1);
                return new DropCommand(tokens[1]);
            case "use":
                checkArgumentsNumber(1, tokens.length - 1);
                return new UseCommand(tokens[1]);
            case "show":
                checkArgumentsNumber(0, tokens.length - 2);
                if (!tokens[1].equals("tables")) {
                    throw new Exception(s + " is unknown command");
                }
                return new ShowTablesCommand();
            case "put":
                checkArgumentsNumber(2, tokens.length - 1);
                return new MultiPutCommand(tokens[1], tokens[2]);
            case "get":
                checkArgumentsNumber(1, tokens.length - 1);
                return new MultiGetCommand(tokens[1]);
            case "remove":
                checkArgumentsNumber(1, tokens.length - 1);
                return new MultiRemoveCommand(tokens[1]);
            case "list":
                checkArgumentsNumber(0, tokens.length - 1);
                return new MultiListCommand();
            case "exit":
                checkArgumentsNumber(0, tokens.length - 1);
                return new ExitCommand();
            default:
                throw new Exception(tokens[0] + " is unknown command");
        }
    }

    public abstract void execute(DataBaseDir base) throws Exception;
}
