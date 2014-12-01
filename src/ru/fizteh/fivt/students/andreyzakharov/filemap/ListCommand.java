package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class ListCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("list: too many arguments");
        }
        if (connector.db.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : connector.db.keySet()) {
                sb.append(key);
                sb.append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        }
    }
}
