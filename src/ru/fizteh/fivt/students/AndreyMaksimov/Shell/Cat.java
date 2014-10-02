package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class Cat extends Instruction {
    public Cat() {
        nameOfInstruction = "cat";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {
        
        if (arguments.length == 1) {
            System.out.println("ERROR: Missing operand");
            System.exit(1);
        }

        String fileName = arguments[1];
        Path needPath = Paths.get(fileName);

        if (!needPath.isAbsolute()) {
            needPath = Paths.get(presentDirectory.toString(), fileName).toAbsolutePath().normalize();
        }
        if (Files.exists(needPath)) {
            if (!Files.isDirectory(needPath)) {
                try (Scanner scanner = new Scanner(Files.newInputStream(needPath))) {
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
                    }
                } catch (IOException e) {
                    System.err.print(e.getMessage());
                    System.exit(1);
                }
            }
        }
        return true;
    }
}

