package com.Shell.commads;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Created by deserg on 21.09.14.
 */



public class Command {

    private Queue<Vector<String>> argumentsQueue;
    private boolean endOfProgram;
    private Path currentPath;

    public Command() {
        argumentsQueue = new LinkedList<>();
        endOfProgram = false;
        currentPath = Paths.get("").toAbsolutePath();
    }

    public boolean isEndOfProgram() {
        return endOfProgram;
    }


    public void readCommands(String[] args) {
        String lineStr = "";
        if (args == null) {
            System.out.print(currentPath.toString() + " $ ");
            Scanner lineScan = new Scanner(System.in);

            lineStr = lineScan.nextLine();
        } else {

            for (String string: args) {
                lineStr += string + " ";
            }

        }

        Scanner blockScan = new Scanner(lineStr);

        blockScan.useDelimiter(";");
        while (blockScan.hasNext()) {
            String commandBlock = blockScan.next();

            Scanner argScan = new Scanner(commandBlock);
            Vector<String> argumentVector = new Vector<>();
            argScan.useDelimiter(" ");

            while (argScan.hasNext()) {
                String nextArg = argScan.next();
                if (!nextArg.equals("")) {
                    argumentVector.add(nextArg);
                }
            }

            if (argumentVector.size() > 0) {
                argumentsQueue.add(argumentVector);
            }
        }

    }


    public void executeAll() {
        if (argumentsQueue.size() == 0) {
            throw new MyException("No command");
        }

        while (argumentsQueue.size() > 0) {
            Vector<String> arguments = new Vector<>(argumentsQueue.poll());

            try {
                executeCommand(arguments);
            } catch (MyException ex) {
                System.out.println(ex.getMessage());
            }

        }


    }


    public void executeCommand(Vector<String> arguments) {

            if (arguments.size() == 0) {
                throw new MyException("No command");
            }

            String command = arguments.get(0);
            switch (command) {

                case "pwd": {
                    CommandPWD(arguments);
                    break;
                }

                case "ls": {
                    CommandLS(arguments);
                    break;
                }

                case "mkdir": {
                    CommandMKDIR(arguments);
                    break;
                }

                case "cd": {
                    CommandCD(arguments);
                    break;
                }

                case "cp": {
                    CommandCP(arguments);
                    break;
                }

                case "rm": {
                    CommandRM(arguments);
                    break;
                }

                case "mv": {
                    CommandMV(arguments);
                    break;
                }

                case "cat": {
                    try {
                        CommandCAT(arguments);
                    } catch (IOException ex) {
                        throw new MyException("cat: I/O exception occurred");
                    }
                    break;
                }


                case "exit": {
                    endOfProgram = true;
                    break;
                }


                default: {
                    throw new MyException("Unknown command");
                }
            }

    }



    private void CommandPWD(Vector<String> arguments) {

        if (arguments.size() > 1) {
            throw new MyException("Too many arguments");
        }

        System.out.println(currentPath.toString());

    }


    private void CommandCD(Vector<String> arguments) {

        if (arguments.size() > 2) {
            throw new MyException("Too many arguments");
        }
        if (arguments.size() == 1) {
            throw new MyException("Not enough arguments");
        }

        try {
            Path newPath = currentPath.resolve(arguments.get(1)).toRealPath();

            if (Files.isDirectory(newPath)) {
                currentPath = newPath;
            } else {
                System.out.println("cd: '" + arguments.get(1) + "': Not a directory");
            }
        } catch (IOException ex) {
            throw new MyException("cd: '" + arguments.get(1) + "': No such file or directory");
        }

    }

    private void CommandLS(Vector<String> arguments) {

        if (arguments.size() > 1) {
            throw new MyException("Too many arguments");
        }

        File curDir = new File(currentPath.toString());
        File[] files = curDir.listFiles();

        if (files != null) {
            for (File file: files) {
                System.out.println(file.getName());
            }
        }
    }



    private void CommandMKDIR(Vector<String> arguments) {

        if (arguments.size() > 2) {
            throw new MyException("Too many arguments");
        }

        if (arguments.size() == 1) {
            throw new MyException("Not enough arguments");
        }

        Path newPath = currentPath.resolve(arguments.get(1));

        try {
            Files.createDirectory(newPath);
        } catch (IOException ex) {
            throw new MyException("Invalid directory name");
        }
    }


    private void CommandCAT(Vector<String> arguments) throws IOException {

        if (arguments.size() < 1) {
            throw new MyException("Not enough arguments");
        } else if (arguments.size() == 2) {

            Path path = Paths.get(arguments.get(1));

            checkExisting(path);

            if (Files.isDirectory(path)) {
                throw new MyException("cat: '" + path.toString() + "': is a directory");
            }

            if (!Files.isReadable(path)) {
                throw new MyException("cat: '" + path.toString() + "': not readable");
            }

            BufferedReader br;

            br = new BufferedReader(new FileReader(path.toString()));

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } else {
            throw new MyException("Too many arguments");
        }


    }

    private void CommandRM(Vector<String> arguments) {

        if (arguments.size() == 1) {
            throw new MyException("Not enough arguments");

        } else if (arguments.size() == 2) {

            deleteNonRecursive(arguments.get(1));

        } else if (arguments.size() == 3) {

            if (!arguments.get(1).equals("-r")) {
                throw new MyException("Invalid command");
            }
            deleteRecursive(arguments.get(2));

        } else {
            throw new MyException("Too many arguments");
        }
    }

    private void deleteNonRecursive(String path) {

        try {
            Path newPath = currentPath.resolve(path).toRealPath();

            if (Files.isDirectory(newPath)) {
                throw new MyException("rm: " + path + ": is a directory");
            } else {
                Files.delete(newPath);
            }
        } catch (IOException ex) {
            throw new MyException("rm: cannot remove '" + path + ": No such file or directory");
        }
    }

    private void deleteRecursive(String path) {

        try {
            Path newPath;
            newPath = currentPath.resolve(path).toRealPath();

            if (Files.isDirectory(newPath)) {
                deleteFinal(newPath);
            } else {
                Files.delete(newPath);
            }

        } catch (IOException ex) {
            throw new MyException("rm: cannot remove '" + path + ": No such file or directory");
        }
    }

    private void deleteFinal(Path path) {
        if (!Files.exists(path)) {
            throw new MyException("Directory does not exists");
        }

        File curDir = new File(path.toString());
        File[] files = curDir.listFiles();

        if (files != null) {
            for (File file: files) {
                if (file.isFile()) {

                    try {
                        Files.delete(file.toPath());
                    } catch (IOException ex) {
                        throw new MyException("I/O error occurs while removing " + file.toPath().toString());
                    }

                }
                if (file.isDirectory()) {
                    deleteFinal(file.toPath());
                }
            }
        }

        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new MyException("I/O error occurs while removing " + path.toString());
        }

    }


    private void CommandMV(Vector<String> arguments) {

        if (arguments.size() < 3) {
            throw new MyException("Not enough arguments");

        } else if (arguments.size() == 3) {

            copyRecursive(arguments.get(1), arguments.get(2));
            deleteRecursive(arguments.get(1));


        } else {
            throw new MyException("Too many arguments");
        }

    }


    private void CommandCP(Vector<String> arguments) {

        if (arguments.size() < 3) {
            throw new MyException("Not enough arguments");

        } else if (arguments.size() == 3) {

            copyNonRecursive(arguments.get(1), arguments.get(2));

        } else if (arguments.size() == 4) {

            if (!arguments.get(1).equals("-r")) {
                throw new MyException("Too many arguments");
            }

            copyRecursive(arguments.get(2), arguments.get(3));

        } else {
            throw new MyException("Too many arguments");
        }

    }


    private void copyNonRecursive(String fromPathString, String toPathString) {


        Path fromPath = resolvePaths(currentPath.toString(), fromPathString);
        Path toPath = resolvePaths(currentPath.toString(), toPathString);
        checkExisting(fromPath);
        checkExisting(toPath);

        if (Files.isDirectory(fromPath)) {
            throw new MyException("rm: " + fromPathString + ": is a directory");
        } else {

            if (Files.isDirectory(toPath)) {
                toPath = Paths.get(toPath.toString(), fromPath.getFileName().toString());
            }

            try {
                Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new MyException("cp: I/O error occurs");
            }


        }

    }

    private void copyRecursive(String fromPathString, String toPathString) {


        Path fromPath;
        Path toPath;

        fromPath = resolvePaths(currentPath.toString(), fromPathString);
        toPath = resolvePaths(currentPath.toString(), toPathString);

        checkExisting(fromPath);
        checkExisting(toPath);

        if (Files.isDirectory(fromPath)) {

            if (!Files.isDirectory(toPath)) {
                throw new MyException("Invalid destination directory");
            }

            toPath = Paths.get(toPath.toString(), fromPath.getFileName().toString());

            try {
                Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new MyException("cp: I/O error occurs");
            }

            copyFinal(fromPath, toPath);

        } else {

            if (Files.isDirectory(toPath)) {
                toPath = Paths.get(toPath.toString(), fromPath.getFileName().toString());
            }

            try {
                Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new MyException("cp: I/O error occurs");
            }
        }


    }




    private void copyFinal(Path fromPath, Path toPath) {

        if (!Files.exists(fromPath) || !Files.exists(toPath)) {
            throw new MyException("Directory does not exists");
        }

        File curDir;
        curDir = new File(fromPath.toString());
        File[] files = curDir.listFiles();

        if (files != null) {
            for (File file: files) {


                Path tempFromPath = Paths.get(fromPath.toString(), file.getName());
                Path tempToPath = Paths.get(toPath.toString(), file.getName());

                try {
                    Files.copy(tempFromPath, tempToPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new MyException("I/O error occurs while coping");
                }

                if (file.isDirectory()) {
                    copyFinal(tempFromPath, tempToPath);
                }
            }
        }

    }





    private void checkExisting(Path path) {

        if (!Files.exists(path)) {
            throw new MyException("File or directory '" + path.toString() + "' does not exists");
        }

    }


    private Path resolvePaths(String path1, String path2) {

        try {
            Path path = Paths.get(path1);
            return path.resolve(path2).toRealPath();
        } catch (IOException ex) {
            throw new MyException("'" + path2 + "': invalid file or directory name");
        }

    }

}
