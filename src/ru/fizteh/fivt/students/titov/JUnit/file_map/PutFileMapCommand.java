package ru.fizteh.fivt.students.titov.JUnit.file_map;

public class PutFileMapCommand extends FileMapCommand {
    public PutFileMapCommand() {
        initialize("put", 3);
    }
    @Override
    public boolean run(FileMap fileMap, String[] args) {
        String oldValue = fileMap.put(args[1], args[2]);
        if (oldValue != null) {
            System.out.println("overwrite\n" + oldValue);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
