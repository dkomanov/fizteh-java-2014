package ru.fizteh.fivt.students.LevkovMiron.MultiFileMap;


import java.util.Scanner;

/**
 * Created by Мирон on 22.09.2014 ru.fizteh.fivt.students.LevkovMiron.shell.
 */
class StreamMultiFileMap extends MultiFileMap {
    StreamMultiFileMap() {
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
