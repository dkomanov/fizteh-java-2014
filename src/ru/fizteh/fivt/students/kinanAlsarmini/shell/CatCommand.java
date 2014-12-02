package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

class CatCommand extends ExternalCommand {
    public CatCommand() {
        super("cat", 1);
    }

    public void execute(String[] args, Shell shell) {
        File target = Utilities.getAbsoluteFile(args[0], shell.getCurrentPath());

        if (!target.exists()) {
            throw new IllegalArgumentException("cat: target file does not exist.");
        }

        if (!target.isFile()) {
            throw new IllegalArgumentException("cat: target is not a valid file.");
        }

        try (
                FileReader reader =
                    new FileReader(target);
                BufferedReader buffer =
                    new BufferedReader(reader)
        ) {
            String line;

            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("cat: cannot read target.");
        }
    }
}
