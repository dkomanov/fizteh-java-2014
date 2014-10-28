package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

/**
 * @author AlexeyZhuravlev
 */
public class ListCommand extends Command {

    public int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBase base) {
        StringBuilder allKeys = new StringBuilder();
        for (String key : base.data.keySet()) {
            if (allKeys.length() > 0) {
                allKeys.append(", ");
            }
            allKeys.append(key);
        }
        System.out.println(allKeys.toString());
    }
}
