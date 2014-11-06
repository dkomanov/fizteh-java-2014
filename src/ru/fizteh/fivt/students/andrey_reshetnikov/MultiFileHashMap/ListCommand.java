package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class ListCommand extends CommandFileMap {

    public String getList(DataBaseOneFile base) {
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
    public void execute(DataBaseOneFile base, Boolean exitStatus) {
        System.out.println(getList(base));
    }

}
