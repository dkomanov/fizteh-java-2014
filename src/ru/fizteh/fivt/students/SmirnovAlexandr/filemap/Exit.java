package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public class Exit extends Command {
    public Exit(String[] a, FileBase data) {
        args = a;
        this.data = data;
    }
    @Override
    public void execute() throws ExceptionStopProcess, ExceptionIncorrectInput {
        if (args.length != 1) {
            System.out.println("Error: Exit haven't any parameters");
            System.out.flush();
            throw new ExceptionIncorrectInput();
        }
        throw new ExceptionStopProcess();
    }
    @Override
    public String name() {
        return "exit";
    }
}
