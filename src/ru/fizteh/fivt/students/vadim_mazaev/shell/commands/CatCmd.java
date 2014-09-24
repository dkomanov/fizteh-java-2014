package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public final class CatCmd {
    private CatCmd() {
        //not called
    }
    public static void run(final String[] cmdWithArgs) throws Exception {
        if (cmdWithArgs.length == 1) {
            throw new Exception(getName() + ": missing operand");
        } else if (cmdWithArgs.length > 2) {
            throw new Exception(getName()
                    + ": too much arguments");
        }
        try {
            File cattedFile = Paths.get(
                    System.getProperty("user.dir"), cmdWithArgs[1]).toFile();
            if (!cmdWithArgs[1].isEmpty() && cattedFile.exists()) {
                if (cattedFile.isFile()) {
                    try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(cattedFile)));) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    }
                    catch (IOException e) {
                        throw new Exception(getName() + ": "
                            + cmdWithArgs[1] + ": cannot read file");
                    }
                } else {
                    throw new Exception(getName() + ": "
                        + cmdWithArgs[1] + ": is a directory");
                }
            } else {
                throw new Exception(getName() + ": "
                    + cmdWithArgs[1] + ": No such file or directory");
            }
        } catch (SecurityException e) {
            throw new Exception(getName()
                + ": cannot open file '" + cmdWithArgs[1] + "': access denied");
        } catch (InvalidPathException e) {
            throw new Exception(getName()
                + ": cannot open file '"
                + cmdWithArgs[1] + "': illegal character in name");
        }
    }
    public static String getName() {
        return "cat";
    }
}
