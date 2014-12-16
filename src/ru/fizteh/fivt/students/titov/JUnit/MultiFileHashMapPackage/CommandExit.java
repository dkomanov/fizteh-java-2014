package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;

public class CommandExit extends CommandMultiFileHashMap{
    public CommandExit() {
        name = "exit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
       System.exit(0);
       return true;
    }
}