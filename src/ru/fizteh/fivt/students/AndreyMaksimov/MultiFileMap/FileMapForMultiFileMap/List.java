package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap;

public class List extends Instruction {

    public List() {
        nameOfInstruction = "list";
    }

    public String getList(DataBase needbase) {
        StringBuilder keys = new StringBuilder();
        for (String key : needbase.needdatabase.keySet()) {
            if (keys.length() > 0) {
                keys.append(", ");
            }
            keys.append(key);
        }
        return keys.toString();
    }

    @Override
    public boolean startNeedInstruction(String[] arguments, DataBase needbase) throws Exception {
        System.out.println(getList(needbase));
        return true;
    }
}
