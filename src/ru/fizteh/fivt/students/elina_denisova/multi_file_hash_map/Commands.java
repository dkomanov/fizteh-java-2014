package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

public enum Commands {

    CREATECOMMAND("create"),
    DROPCOMMAND("drop"),
    USECOMMAND("use"),
    SHOWCOMMAND("show"),
    PUTCOMMAND("put"),
    GETCOMMAND("get"),
    REMOVECOMMAND("remove"),
    LISTCOMMAND("list"),
    EXITCOMMAND("exit");


    private String value;
    private Commands(String word) {
        value = word;
    }

    public static Commands getCommand(String word) throws IllegalArgumentException {
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
