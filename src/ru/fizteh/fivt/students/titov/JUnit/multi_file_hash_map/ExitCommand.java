package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

public class ExitCommand extends MultiFileHashMapCommand {
    public ExitCommand() {
        initialize("exit", 1);
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
       System.exit(0);
       return true;
    }
}
