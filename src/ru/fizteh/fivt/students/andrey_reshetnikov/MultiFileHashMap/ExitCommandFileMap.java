package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class ExitCommandFileMap extends CommandFileMap {

    public int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBase base) throws ExitCommandException {
        throw new ExitCommandException();
    }
}

