package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

/**
 * @author AlexeyZhuravlev
 */
public class GetCommand extends Command {

    private String key;

    public int numberOfArguments() {
        return 1;
    }

    public void putArguments(String[] args) {
        key = args[1];
    }

    @Override
    public void execute(DataBase base) {
        String value = base.data.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
