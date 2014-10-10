package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

public class RemoveCommand extends Command {
    public RemoveCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 2) {
            throw new IllegalArgumentException("remove: wrong syntax");
        }
        String value = hashMap.remove(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
