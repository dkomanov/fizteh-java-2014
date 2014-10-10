package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

public class PutCommand extends Command {
    public PutCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 3) {
            throw new IllegalArgumentException("put: wrong syntax");
        }
        String oldValue = hashMap.put(args[1], args[2]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
