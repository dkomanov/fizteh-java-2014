package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

public class GetCommand extends Command {
    public GetCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 2) {
            throw new WrongSyntaxException("get");
        }
        String value = hashMap.get(args[1]);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("found" + System.lineSeparator() + value);
        }
    }
}
