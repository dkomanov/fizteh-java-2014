package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Дмитрий on 09.10.14.
 */
public class Welcome {

    public static void main(String[] args) {
        Path dbPath;

        try {
            dbPath = new File(System.getProperty("fizteh.db.dir")).toPath().normalize();
        } catch (NullPointerException e) {
            System.err.println("Your directory is null");
            System.exit(-1);
            return;
        }
        Modes mode = new Modes(dbPath);
        if (args.length != 0) {
            mode.batchMode(args);
        } else {
            mode.interactiveMode();
        }
    }
}
