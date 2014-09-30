package ru.fizteh.fivt.students.LevkovMiron.shell;


/**
 * Левков Мирон, 394 группа.
 */
class Main {

    public static void main(final  String[] args) {
        if (args.length > 0) {
            new PacketShell().readCommands(args);
        } else {
            new StreamShell().readCommands();
        }
    }
}