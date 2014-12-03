package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Shell {
    private boolean terminated;
    private Path currentPath;
    private ExternalCommand[] possibleCommands;

    public Shell(ExternalCommand[] possibleCommands) {
        this.possibleCommands = possibleCommands;

        terminated = false;

        currentPath = Utilities.getAbsolutePath(Paths.get(""));
    }

    public void terminate() {
        terminated = true;
    }

    private void runCommand(String command) throws IOException {
        String[] tokens = command.split("\\s+");

        if (tokens.length == 0) {
            throw new IllegalArgumentException("Empty command.");
        }

        boolean foundCommand = false;
        for (ExternalCommand ex: possibleCommands) {
            if (tokens[0].equals(ex.getName())) {
                foundCommand = true;

                if (!ex.checkArgNumber(tokens.length - 1)) {
                    throw new IllegalArgumentException(tokens[0] + " does not take "
                            + (tokens.length - 1) + " argument(s).");
                }

                ex.execute(Arrays.copyOfRange(tokens, 1, tokens.length), this);
            }
        }

        if (!foundCommand) {
            throw new IllegalArgumentException("Unknown command.");
        }
    }

    public void changeDirectory(String extPath) {
        Path pextPath = Paths.get(extPath).normalize();
        Path tempPath = Utilities.joinPaths(currentPath, pextPath);

        if (Files.notExists(tempPath)) {
            throw new IllegalArgumentException("cd: Invalid directory.");
        }

        currentPath = tempPath;
    }

    public Path getCurrentPath() {
        return Paths.get(currentPath.toString());
    }

    public void startInteractive() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (!terminated) {
            try {
                System.out.print("$ ");

                String currentCommand = in.readLine();

                if (currentCommand == null) {
                    break;
                }

                runCommands(currentCommand);
            } catch (IllegalArgumentException | IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void startBatch(String commands) {
        try {
            runCommands(commands);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void runCommands(String mergedCommands) throws IOException {
        if (mergedCommands.trim().equals("")) {
            return;
        }

        String[] commands = mergedCommands.trim().split("\\s*;\\s*");

        for (int i = 0; i < commands.length && !terminated; i++) {
            runCommand(commands[i]);
        }
    }
}
