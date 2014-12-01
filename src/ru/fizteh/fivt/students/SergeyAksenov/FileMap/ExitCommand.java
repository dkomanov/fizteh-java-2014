package ru.fizteh.fivt.students.SergeyAksenov.FileMap;


public class ExitCommand implements Command {
    public void run(final String[] args, DataBase dataBase, Environment env)
            throws FileMapException, FileMapExitException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            dataBase.close();
            ErrorHandler.countArguments("exit");
        }
        dataBase.close();
        throw new FileMapExitException();
    }
}
