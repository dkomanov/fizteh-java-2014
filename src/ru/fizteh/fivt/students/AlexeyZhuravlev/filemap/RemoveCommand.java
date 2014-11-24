package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

/**
 * @author AlexeyZhuravlev
 */
public class RemoveCommand extends Command {

    private String key;

    public void putArguments(String[] args) {
        key = args[1];
    }

    public int numberOfArguments() {
        return 1;
    }

    public RemoveCommand(String passedKey) {
        key = passedKey;
    }

    public RemoveCommand() {
    }

    @Override
    public void execute(DataBase base) throws Exception {
        if (base.data.containsKey(key)) {
            base.data.remove(key);
            base.sync();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
