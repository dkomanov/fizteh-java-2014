package ru.fizteh.fivt.students.YaronskayaLiubov.FileMap;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 06.10.14.
 */
public class ExitCommand extends Command {
    ExitCommand() {
        name = "exit";
        numberOfArguements = 1;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        try {
            FileMap.save();
        } catch (IOException e) {
            System.err.println("Error writing file");
        }
        System.exit(FileMap.errorOccurred ? 1 : 0);
        return true;
    }
}

