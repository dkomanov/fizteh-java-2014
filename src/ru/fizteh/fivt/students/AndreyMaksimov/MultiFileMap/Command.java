package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;


public abstract class Command {

    private static void checkArgumentsNumber(int real, int now) throws Exception {
        if (real != now) {
            throw new Exception("ERROR: Wrong number of arguments: " + String.valueOf(real) + " expected");
        }
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("ERROR: Unfortunately not some commands");
        }
        String[] needArguments = s.replaceFirst(" *", "").split("\\s+");
        switch (needArguments[0]) {

            case "use":
                checkArgumentsNumber(1, needArguments.length - 1);
                return new Use(needArguments[1]);
            case "drop":
                checkArgumentsNumber(1, needArguments.length - 1);
                return new Drop(needArguments[1]);
            case "show":
                checkArgumentsNumber(0, needArguments.length - 2);
                if (!needArguments[1].equals("tables")) {
                    throw new Exception(s + " ERROR: Unknown command");
                }
                return new ShowTables();
            case "create":
                checkArgumentsNumber(1, needArguments.length - 1);
                return new Create(needArguments[1]);

            case "put":
                checkArgumentsNumber(2, needArguments.length - 1);
                return new PutMulti(needArguments[1], needArguments[2]);
            case "get":
                checkArgumentsNumber(1, needArguments.length - 1);
                return new GetMulti(needArguments[1]);
            case "list":
                checkArgumentsNumber(0, needArguments.length - 1);
                return new ListMulti();
            case "exit":
                checkArgumentsNumber(0, needArguments.length - 1);
                return new ExitMulti();
            case "remove":
                checkArgumentsNumber(1, needArguments.length - 1);
                return new RemoveMulti(needArguments[1]);
            default:
                throw new Exception(needArguments[0] + " ERROR: Unknown command");
        }
    }

    public abstract void startNeedMultiInstruction(DataBaseDir base) throws Exception;
}

