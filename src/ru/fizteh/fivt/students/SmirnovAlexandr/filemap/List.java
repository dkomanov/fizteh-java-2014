package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public class List extends Command {
    public List(String[] a, FileBase data) {
        args = a;
        this.data = data;
    }

    @Override
    public void execute() throws ExceptionIncorrectInput {
        if (args.length != 1) {
            System.out.println("Error: List haven't any parameters");
            System.out.flush();
            throw new ExceptionIncorrectInput();
        }
        StringBuilder allkeys = new StringBuilder();
        for (String k : data.map.keySet()) {
            if (allkeys.length() > 0) {
                allkeys.append(", ");
            }
            allkeys.append(k);
        }
        System.out.println(allkeys.toString());
    }

    @Override
    public String name() {
        return "list";
    }
}

