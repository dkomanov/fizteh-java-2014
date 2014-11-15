package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;


public class Exit extends Command {

    public int numberOfArguments() {
        return 0;
    }

    @Override
    public void startNeedInstruction(DataBase base) throws Exception {
        throw new ExitException();
    }
}