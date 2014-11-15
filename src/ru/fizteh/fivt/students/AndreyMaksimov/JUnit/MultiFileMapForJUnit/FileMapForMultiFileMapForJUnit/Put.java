package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

public class Put extends Command {

    private String key;
    private String value;

    public void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }

    public int numberOfArguments() {
        return 2;
    }

    public Put(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    public Put() {
    }

    @Override
    public void startNeedInstruction(DataBase base) throws Exception {
        String baseValue = base.data.get(key);
        if (baseValue != null) {
            System.out.println("overwrite");
            System.out.println(baseValue);
        } else {
            System.out.println("new");
        }
        base.data.put(key, value);
        base.sync();
    }
}