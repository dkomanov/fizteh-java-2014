package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;



public class Mkdir extends Instruction {
    public Mkdir() {
        nameOfInstruction = "mkdir";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {

       if (arguments.length == 1) {
                System.out.println("ERROR: Missing operand");
                System.exit(1);
            }

        Path needPath = Paths.get(presentDirectory.toString(), arguments[1]).toAbsolutePath().normalize();
        if (!Files.exists(needPath)) {
            try {
                Files.createDirectory(needPath);
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        } else {
            System.err.print("ERROR: File already exists");
            System.exit(1);
        }
        return true;
    }
}

