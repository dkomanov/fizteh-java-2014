package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

public class RemoveCommand extends Command {
    public RemoveCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 2) {
            throw new WrongSyntaxException("remove");
        }
        String value = hashMap.remove(args[1]);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("removed");
        }
    }
}
