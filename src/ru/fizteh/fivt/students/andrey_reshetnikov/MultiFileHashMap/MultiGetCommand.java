package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class MultiGetCommand extends Command {
    private final String key;

    public MultiGetCommand(String newKey) {
        key = newKey;
    }

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            int hashCode = Math.abs(key.hashCode());
            int ndirectory = hashCode % modul;
            int nfile = hashCode / modul % modul;
            GetCommand get = new GetCommand(key);
            DataBaseOneFile dataBase = base.getUsing().databases[ndirectory][nfile];
            if (dataBase == null) {
                System.out.println("not found");
            } else {
                get.execute(base.getUsing().databases[ndirectory][nfile], false);
            }
        }
    }
}
