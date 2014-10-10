package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

public class GetCommand extends Command {
    public GetCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 2) {
            throw new IllegalArgumentException("put: wrong syntax");
        }
        String value = hashMap.get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
