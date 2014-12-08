package ru.fizteh.fivt.students.LevkovMiron.MultiFileMap;


/**
 * Левков Мирон, 394 группа.
 */
class Main {

    public static void main(final  String[] args) {
        if (args.length > 0) {
            new PacketMultiFileMap().readCommands(args);
        } else {
            new StreamMultiFileMap().readCommands();
        }
    }
}
