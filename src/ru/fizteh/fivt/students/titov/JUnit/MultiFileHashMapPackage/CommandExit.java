package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

public class CommandExit extends CommandMultiFileHashMap {
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
