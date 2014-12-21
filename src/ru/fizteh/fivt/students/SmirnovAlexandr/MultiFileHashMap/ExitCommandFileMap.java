package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

public class ExitCommandFileMap extends CommandFileMap {

    public int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBase base) throws ExceptionExitCommand {
        throw new ExceptionExitCommand();
    }
}

