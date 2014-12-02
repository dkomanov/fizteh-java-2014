package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public class Remove extends Command {
    public Remove(String[] a, FileBase data) {
        args = a;
        this.data = data;
    }

    @Override
    public void execute() throws ExceptionIncorrectInput {
        if (args.length != 2) {
            System.out.println("Error: remove accepts not 2 parameters");
            System.out.flush();
            throw new ExceptionIncorrectInput();
        }
        if (data.map.remove(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    @Override
    public String name() {
        return "remove";
    }
}
