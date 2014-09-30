package ru.fizteh.fivt.students.LevkovMiron.shell;

import java.util.Scanner;

/**
 * Created by Мирон on 22.09.2014 ru.fizteh.fivt.students.LevkovMiron.shell.
 */
class StreamFileMap extends FileMap {
    StreamFileMap() {
        super(System.out);
    }
    public void readCommands() {
        System.out.println("$");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] commands = input.split(";");
        for (String cmd : commands) {
            runCommand(cmd, System.out);
        }
        readCommands();
    }
}