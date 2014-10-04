package ru.fizteh.fivt.students.ganiev.shell;

import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import java.lang.Thread;

public class Shell {
    private Shell() {}

    public static class MyShell {
        public MyShell(String currentDirectory) {
            this.currentDirectory = currentDirectory;
        }

        public void changeCurrentDirectory(String newCurrentDirectory) {
            currentDirectory = newCurrentDirectory;
        }

        public String getCurrentDirectory() {
            return currentDirectory;
        }

        private String currentDirectory;
    }

    private static void invokeInstruction(String[] instruction, Shell.MyShell shell) throws IOException, BreakingException {
        Command currentCommand = null;

        if (instruction[0].equals("cd")) {
            currentCommand = new ChangeDirectory();
        } else if (instruction[0].equals("mkdir")) {
            currentCommand = new MakeDirectory();
        } else if (instruction[0].equals("pwd")) {
            currentCommand = new PrintWorkingDirectory();
        } else if (instruction[0].equals("rm")) {
            if (instruction.length == 2) {
                currentCommand = new Remove(1);
            } else if (instruction.length == 3) {
                currentCommand = new Remove(2);
            }
        } else if (instruction[0].equals("cp")) {
            if (instruction.length == 3) {
                currentCommand = new Copy(2);
            } else if (instruction.length == 4) {
                currentCommand = new Copy(3);
            }
        } else if (instruction[0].equals("mv")) {
            currentCommand = new Move();
        } else if (instruction[0].equals("ls")) {
            currentCommand = new Ls();
        } else if (instruction[0].equals("cat")) {
            currentCommand = new Catenate();
        } else if (instruction[0].equals("exit")) {
            currentCommand = new Exit();
        }

        if (currentCommand == null) {
            throw new IOException("Unknown instruction: " + instruction[0]);
        }

        if (instruction.length > currentCommand.getNumberOfArguments() + 1) {
            throw new IOException("Too many arguments for " + instruction[0]);
        } else if (instruction.length < currentCommand.getNumberOfArguments() + 1) {
            throw new IOException("Too less arguments for " + instruction[0]);
        }

        currentCommand.invokeCommand(Arrays.copyOfRange(instruction, 1, instruction.length), shell);
    }

    public static void main(String[] args) {
        MyShell shell = new MyShell(System.getProperty("user.dir"));

        if (args.length != 0) {
            StringBuilder strBuilderOfInstructions = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                strBuilderOfInstructions.append(args[i] + " ");
            }
            String instructions = strBuilderOfInstructions.toString();

            try {
                for (String instruction : instructions.trim().split("\\s*;\\s*", -1)) {
                    invokeInstruction(instruction.split("\\s+"), shell);
                }
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
                System.exit(1);
            } catch (BreakingException exception) {
                System.exit(0);
            }
        } else {
            Scanner inputScanner = new Scanner(System.in);

            while (!Thread.currentThread().isInterrupted()) {
                System.out.print(shell.getCurrentDirectory() + "$ ");

                String instructions = inputScanner.nextLine();

                try {
                    for (String instruction : instructions.trim().split("\\s*;\\s*", -1)) {
                        invokeInstruction(instruction.split("\\s+"), shell);
                    }
                } catch (IOException exception) {
                    System.err.println(exception.getMessage());
                } catch (BreakingException exception) {
                    System.exit(0);
                }
            }
        }
    }
}