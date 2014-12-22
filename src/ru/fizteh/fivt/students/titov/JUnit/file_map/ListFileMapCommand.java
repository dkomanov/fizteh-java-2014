package ru.fizteh.fivt.students.titov.JUnit.file_map;

import java.util.List;

public class ListFileMapCommand extends FileMapCommand {
    public ListFileMapCommand() {
        initialize("list", 1);
    }

    @Override
    public boolean run(FileMap fileMap, String[] args) {
        List<String> allKeys = fileMap.list();
        System.out.println(String.join(", ", allKeys));
        return true;
    }
}
