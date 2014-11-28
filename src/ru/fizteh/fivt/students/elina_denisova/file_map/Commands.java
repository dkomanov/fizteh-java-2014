package ru.fizteh.fivt.students.elina_denisova.file_map;

public enum Commands {

    PUTCOMMAND("put"),
    GETCOMMAND("get"),
    REMOVECOMMAND("remove"),
    LISTCOMMAND("list"),
    EXITCOMMAND("exit");

    private String value;
    private Commands(String word) {
        value = word;
    }

    public static Commands getCommand(String word) throws IllegalArgumentException{
        for (Commands command : Commands.values()) {
            if (command.getValue().equals(word)) {
                return command;
            }
        }
        throw new IllegalArgumentException(word + " - unknown command");
    }

    public String getValue() {
        return value;
    }
}
