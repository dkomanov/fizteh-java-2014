package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

public class CommandShowTables extends CommandMultiFileHashMap {
    public CommandShowTables() {
        name = "show";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (!args[1].equals("tables")) {
            System.err.println(name + ": wrong arguments");
            return false;
        }
        myMap.showTables();
        return true;
    }
}
