package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Cd extends Instruction {
    public Cd() {
        nameOfInstruction = "cd";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {
        if (arguments.length == 1) {
            presentDirectory = Paths.get("").toAbsolutePath().normalize();
            return true;
        }

        Path needAbsolutePath = Paths.get(arguments[1]);

        if (!needAbsolutePath.isAbsolute()) {
            needAbsolutePath = Paths.get(presentDirectory.toString(), arguments[1]).toAbsolutePath().normalize();
        }

        if (Files.exists(needAbsolutePath) && Files.isDirectory(needAbsolutePath)) {
            presentDirectory = needAbsolutePath;
        } else {
            System.out.println("ERROR: No such Directory or File");
        }
        return true;
    }
}
