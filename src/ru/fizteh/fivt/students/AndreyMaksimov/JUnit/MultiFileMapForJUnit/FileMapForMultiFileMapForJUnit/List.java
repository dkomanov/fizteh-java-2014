package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

public class List extends Command {

    public int numberOfArguments() {
        return 0;
    }

    public List() {
    }

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
    public void startNeedInstruction(DataBase base) {
        System.out.println(getList(base));
    }
}