package ru.fizteh.fivt.students.titov.JUnit.file_map;

public class GetFileMapCommand extends FileMapCommand {
    public GetFileMapCommand() {
        initialize("get", 2);
    }

    @Override
    public boolean run(FileMap fileMap, String[] args) {
        String value = fileMap.get(args[1]);
        if (value != null) {
            System.out.println("found");
            System.out.println(value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
