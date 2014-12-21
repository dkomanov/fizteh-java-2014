package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by ВАНЯ on 19.12.2014.
 */
public class Main {
    public static void main(String[] args) {
        Path dbPath;

        try {
            dbPath = new File(System.getProperty("fizteh.db.dir")).toPath().normalize();
            new Welcome(dbPath, args);
        } catch (NullPointerException e) {
            System.err.println("Your directory is null");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Some IOException: " + e.getMessage());
        }
    }
}
