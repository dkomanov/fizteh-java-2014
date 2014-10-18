package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public class ListCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("list: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        if (connector.activeTable.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : connector.activeTable.keySet()) {
                sb.append(key);
                sb.append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        }
    }
}
