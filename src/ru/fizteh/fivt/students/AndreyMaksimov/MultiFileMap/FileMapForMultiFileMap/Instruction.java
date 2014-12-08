package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap;

public abstract class Instruction {
    protected String nameOfInstruction;

    public abstract boolean startNeedInstruction(String[] arguments, DataBase needbase) throws  Exception;

}
