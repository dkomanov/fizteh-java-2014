package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.util.Scanner;
import java.util.HashMap;

public class Shell {
    private HashMap<String, Instruction> instructions;

    public Shell() {
        instructions = new HashMap();

        Instruction commandCd = new Cd();
        instructions.put(commandCd.nameOfInstruction, commandCd);
        Instruction commandLs = new Ls();
        instructions.put(commandLs.nameOfInstruction, commandLs);
        Instruction commandRm = new Rm();
        instructions.put(commandRm.nameOfInstruction, commandRm);
        Instruction commandCp = new Cp();
        instructions.put(commandCp.nameOfInstruction, commandCp);
        Instruction commandMv = new Mv();
        instructions.put(commandMv.nameOfInstruction, commandMv);
        Instruction commandCat = new Cat();
        instructions.put(commandCat.nameOfInstruction, commandCat);
        Instruction commandPwd = new Pwd();
        instructions.put(commandPwd.nameOfInstruction, commandPwd);
        Instruction commandExit = new Exit();
        instructions.put(commandExit.nameOfInstruction, commandExit);
        Instruction commandMkdir = new Mkdir();
        instructions.put(commandMkdir.nameOfInstruction, commandMkdir);

    }

    public boolean interactive() {
        System.out.print("$ ");

        String[] parsedCommands;
        String[] needArguments;
        boolean checkShell = true;

        try (Scanner IN = new Scanner(System.in)) {
            while (checkShell) {
                if (IN.hasNextLine()) {
                    parsedCommands = IN.nextLine().split(";");
                } else {
                    continue;
                }
                for (String firstCommand : parsedCommands) {

                    firstCommand = firstCommand.trim(); //Remove unnecessary whitespace;
                    needArguments = firstCommand.split("\\s+");

                    Instruction shellInstruction = instructions.get(needArguments[0]);
                    if (shellInstruction == null) {
                        System.out.println(needArguments[0] + "ERROR: No such command");
                        checkShell = false;
                    } else {
                        shellInstruction.startNeedInstruction(needArguments);
                        checkShell = true;
                    }

                }
            }

        }

        return checkShell;
    }

    public boolean batch(String[] arguments) {
        String[] parsedCommands;
        String[] needArguments;
        boolean checkShell = true;
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
                checkShell = false;
            } else {
                shellInstruction.startNeedInstruction(needArguments);
                checkShell = true;
            }
        }
        return checkShell;
    }

}
