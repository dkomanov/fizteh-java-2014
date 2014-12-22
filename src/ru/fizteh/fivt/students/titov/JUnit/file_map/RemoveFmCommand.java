package ru.fizteh.fivt.students.titov.JUnit.file_map;

public class RemoveFmCommand extends FileMapCommand {
    public RemoveFmCommand() {
        initialize("remove", 2);
    }
    @Override
    public boolean run(FileMap fileMap, String[] args) {
        String value = fileMap.remove(args[1]);
        if (value != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}

