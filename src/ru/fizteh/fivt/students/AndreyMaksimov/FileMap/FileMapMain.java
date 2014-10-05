package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;

import java.util.Scanner;
import java.util.HashMap;

public class FileMapMain {

    private HashMap<String, Instruction> instructions;

    public FileMapMain() {
        instructions = new HashMap<String, Instruction>();

        Instruction commandPut = new Put();
        instructions.put(commandPut.nameOfInstruction, commandPut);
    }

    public void main(String[] arguments) {

        boolean check = true;

        String needPath = System.getProperty("db.file");
        if (needPath == null) {
            System.err.println("ERROR: No such path");
            System.exit(1);
        }
        try {
            DataBase needBase = new DataBase(needPath);
            if (arguments.length == 0) {
                check = interactive(needBase);
            } else {
                check = batch(needBase, arguments);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (!check) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }


    private boolean interactive(DataBase needBase) {
        System.out.print("$ ");

        boolean checkfilemap = true;
        String[] parsedArguments;
        String[] needArguments;

        Scanner in = new Scanner(System.in);
        while (checkfilemap) {
            if (in.hasNextLine()) {
                parsedArguments = in.nextLine().split(";");
            } else {
                continue;
            }
            for (String firstArgument : parsedArguments) {

                firstArgument = firstArgument.trim();
                needArguments = firstArgument.split("\\s+");
                try {
                    Instruction fileMapInstruction = instructions.get(needArguments[0]);
                    if (fileMapInstruction == null) {
                        System.out.println(needArguments[0] + "ERROR: No such command");
                        checkfilemap = false;
                    } else {
                        fileMapInstruction.startNeedInstruction(needArguments, needBase);
                        checkfilemap = true;

                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
        return checkfilemap;
    }

    private boolean batch(DataBase needbase, String[] arguments) throws Exception {

        String[] parsedCommands;
        String[] needArguments;
        boolean checkfilemap = true;

        String setOfInstructions = arguments[0];
        for (int i = 1; i < arguments.length; ++i) {
            setOfInstructions = setOfInstructions + " " + arguments[i];
        }

        parsedCommands = setOfInstructions.split(";");

        for (String oneCommand : parsedCommands) {
            needArguments = oneCommand.trim().split("\\s");

            Instruction shellInstruction = instructions.get(needArguments[0]);
            if (shellInstruction == null) {
                System.out.println(needArguments[0] + "ERROR: No such command");
                checkfilemap = false;
            } else {
                shellInstruction.startNeedInstruction(needArguments, needbase);
                checkfilemap = true;
            }
        }
        return checkfilemap;
    }

}
