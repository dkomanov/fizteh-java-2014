package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

public class ExitMulti extends Command {

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        throw new ExitException();
    }
}

