package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class GetCommand extends CommandFileMap {

    private String key;

    public GetCommand(String newKey) {
        key = newKey;
    }

    @Override
    public void execute(DataBaseOneFile base, Boolean exitStatus) {
        String value = base.data.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
