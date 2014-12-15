package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap;

public class ListCommand extends CommandFileMap {

    public int numberOfArguments() {
        return 0;
    }

    public ListCommand() {}

    public String getList(DataBase base) {
        StringBuilder allKeys = new StringBuilder();
        for (String key : base.data.keySet()) {
            if (allKeys.length() > 0) {
                allKeys.append(", ");
            }
            allKeys.append(key);
        }
        return allKeys.toString();
    }

    @Override
    public void execute(DataBase base) {
        System.out.println(getList(base));
    }
}
