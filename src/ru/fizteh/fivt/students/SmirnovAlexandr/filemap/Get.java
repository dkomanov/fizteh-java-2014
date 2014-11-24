package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public class Get extends Command {
    public Get(String[] a, FileBase data) {
        args = a;
        this.data = data;
    }
    @Override
    public void execute() throws ExceptionIncorrectInput {
        if (args.length != 2) {
            System.out.println("Error: Get accepts not 2 parameter");
            System.out.flush();
            throw new ExceptionIncorrectInput();
        }
        String value = data.map.get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println(value);
        }
    }

    @Override
    public String name() {
        return "get";
    }
}
