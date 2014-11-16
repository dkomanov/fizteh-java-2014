package ru.fizteh.fivt.students.LevkovMiron.FileMap;


import java.io.*;
/**
 * Левков Мирон, 394 группа.
 */
class Main {

    public static void main(final  String[] args) {
        if (args.length > 0) {
            new PacketFileMap().readCommands(args);
        } else {
            new StreamFileMap().readCommands();
        }
    }
}
