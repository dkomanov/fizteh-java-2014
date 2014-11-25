package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public class Put extends Command {
    public Put(String[] a, FileBase data) {
        args = a;
        this.data = data;
    }

    @Override
    public void execute() throws ExceptionIncorrectInput {
        if (args.length != 3) {
            System.out.println("Error: Put accepts not 3 parameters");
            System.out.flush();
            throw new ExceptionIncorrectInput();
        }
        String s = data.map.put(args[1], args[2]);
        if (s == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(s);
        }
    }

    @Override
    public String name() {
        return "put";
    }
}
