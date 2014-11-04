package ru.fizteh.fivt.students.AliakseiSemchankau.shell; /**
 * Created by Aliaksei Semchankau on 29.09.2014.
 */

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;

public class CommandLine {

    boolean mustExit;
    private Queue<Vector<String>> listOfCommands;
    Path currentDirectory;

    public CommandLine() {
        mustExit = false;
        currentDirectory = Paths.get("").toAbsolutePath();
        listOfCommands = new LinkedList<Vector<String>>();
    }

    public boolean isExit() {
        return mustExit;
    }

    public void readCommands(String[] args) {
        String toParse = "";
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                toParse += (args[i] + " ");
            }
        } else {
            Scanner line = new Scanner(System.in);
            toParse = line.nextLine();
        }

        if (toParse.length() == 0) {
            return;
        }

            String curArgument = "";

            for (int curSymbol = 0; ; ++curSymbol) {
                if (curSymbol != toParse.length() && toParse.charAt(curSymbol) != ';') {
                    curArgument += toParse.charAt(curSymbol);
                } else {
                    listOfCommands.add(processing(curArgument));
                    curArgument = "";
                    if (curSymbol == toParse.length()) {
                        break;
                    }
                }

        }

       /*while (!listOfCommands.isEmpty())
        {
            Vector<String> curLine = listOfCommands.poll();
            for (int i = 0; i < curLine.size(); ++i)
                System.out.print(curLine.get(i) + " ");
            System.out.println(";");
        }*/
    }

    Vector<String> processing(String argLine) {
        Vector<String> argumentList = new Vector<String>();
        String argument = new String();
        argument = "";
        for (int symbol = 0; ; ++symbol) {
            if (symbol != argLine.length() && argLine.charAt(symbol) != ' ') {
                argument += argLine.charAt(symbol);
            } else {
                if (argument.length() > 0) {
                    argumentList.add(argument);
                    argument = "";
                }

                if (symbol == argLine.length()) {
                    break;
                }
            }
        }
        return argumentList;
    }

    public void doCommands() {
        while (!listOfCommands.isEmpty()) {
            Vector<String> currentCommand = listOfCommands.poll();
            String toDo = currentCommand.get(0);
            //System.out.println(toDo);

            if (toDo.equals("pwd")) {
                doPWD(currentCommand);
            } else
            if (toDo.equals("ls")) {
                    doLS(currentCommand);
            } else
            if (toDo.equals("exit")) {
                    doExit(currentCommand);
            } else
            if (toDo.equals("cd")) {
                    doCD(currentCommand);
            } else
            if (toDo.equals("mkdir")) {
                    doMKDIR(currentCommand);
            } else
            if (toDo.equals("cp")) {
                    doCP(currentCommand);
            } else
            if (toDo.equals("rm")) {
                    doRM(currentCommand);
            } else
            if (toDo.equals("mv")) {
                    doMV(currentCommand);
            } else {
                throw new ShellException("unknown command");
            }
        }
    }

    public void doPWD(Vector<String> args) {
        if (args.size() > 1) {
            throw new ShellException("to much arguments for pwd");
        }

        System.out.println(currentDirectory);
    }

    public void doLS(Vector<String> args) {
        if (args.size() > 1) {
            throw new ShellException("too much arguments for ls");
        }

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory);
            for (Path currentFile : stream) {
                System.out.println(currentFile.getFileName().toString());
            }
        } catch (IOException exception) {
            throw new ShellException("something wrong with listing all files");
        }

    }

    public void doExit(Vector<String> args) {
        if (args.size() > 1) {
            throw new ShellException("too much arguments for exit");
        }

        mustExit = true;
    }

    public void doCD(Vector<String> args) {

        if (args.size() > 2) {
            throw new ShellException("too much arguments for cd");
        }

        if (args.size() == 1) {
            throw new ShellException("there are no arguments for cd");
        }

        try {
            Path directoryToChange = currentDirectory.resolve(args.get(1)).toRealPath();

            if (Files.isDirectory(directoryToChange)) {
                currentDirectory = directoryToChange;
            } else {
                throw new ShellException(args.get(1) + " is not a direction");
            }
        } catch (IOException exc) {
            throw new ShellException("'" + args.get(1) + "' is not a file or directory");
        }
    }

    public void doMKDIR(Vector<String> args) {
        if (args.size() > 2) {
            throw new ShellException("too much arguments for mkdir");
        }

        if (args.size() == 1) {
            throw new ShellException("there are no arguments for mkdir");
        }

        Path newDirectory = currentDirectory.resolve(args.get(1));

        try {
            Files.createDirectory(newDirectory);
        } catch (IOException exc) {
            throw new ShellException("wrong directory name");
        }
    }

    public void doCP(Vector<String> args) {

        if (args.size() == 1) {
            throw new ShellException("no arguments for cp");
        }

        boolean makeRecursion = false;

        if (args.get(1).equals("-r")) {
            makeRecursion = true;
        }

        //System.out.println(makeRecursion);

        if (makeRecursion && args.size() < 4) {
            throw new ShellException("not enough arguments for recursive cp");
        }

        if (makeRecursion && args.size() > 4) {
            throw new ShellException("too much arguments for recursive cp");
        }

        if (!makeRecursion && args.size() < 3) {
            throw new ShellException("not enough arguments for nonrecursive cp");
        }

        if (!makeRecursion && args.size() > 3) {
            throw new ShellException("too much arguments for nonrecursive cp");
        }

        if (makeRecursion) {
            doRecursianCP(args);
        }

        if (!makeRecursion) {
            doNonRecursianCP(args);
        }

    }

    public void doRecursianCP(Vector<String> args) {

        Path objectToCopy = currentDirectory.resolve(args.get(2)).toAbsolutePath();
        Path addressToCopy = currentDirectory.resolve(args.get(3)).toAbsolutePath();

        objectToCopy.normalize();
        addressToCopy.normalize();

        if (subString(objectToCopy.toString(), addressToCopy.toString())) {
            throw new ShellException(objectToCopy + "you are trying to copy a directory inside it(doRecursianCP)");
        }

        if (!Files.exists(objectToCopy)) {
            throw new ShellException(objectToCopy + "something wrong with that file(doRecursianCP)");
        }


        if (!Files.isDirectory(addressToCopy)) {
            throw new ShellException(addressToCopy + " adress is not a direction");
        }

        addressToCopy = Paths.get(addressToCopy.toString(), objectToCopy.getFileName().toString());

        try {
            Files.copy(objectToCopy, addressToCopy, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exc) {
            throw new ShellException("unsucsessful trying to copy");
        }

        if (Files.isDirectory(objectToCopy)) {
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(objectToCopy);

                for (Path currentFile : stream) {
                    Path newObjectToCopy = objectToCopy;
                    newObjectToCopy = Paths.get(newObjectToCopy.toString(), currentFile.getFileName().toString());
                    Vector<String> innerDirection = new Vector<String>();
                    innerDirection.add(args.get(0));
                    innerDirection.add(args.get(1));
                    innerDirection.add(newObjectToCopy.toString());
                    Path newAddressToCopy = addressToCopy;
                    innerDirection.add(newAddressToCopy.toString());
                    doCP(innerDirection);
                }
            } catch (IOException exc) {
                throw new ShellException("unable to do recursive cp because failed with opening of directory");
            }
        }

    }

    public void doNonRecursianCP(Vector<String> args) {
        Path objectToCopy = currentDirectory.resolve(args.get(1)).toAbsolutePath();
        Path addressToCopy = currentDirectory.resolve(args.get(2)).toAbsolutePath();

        objectToCopy.normalize();
        addressToCopy.normalize();

        if (!Files.exists(objectToCopy)) {
            throw new ShellException(objectToCopy + " something wrong with that file(doNonRecursianCP)");
        }

        if (Files.isDirectory(objectToCopy)) {
            throw new ShellException(objectToCopy + " nonrekursive cp for directory(doNonRecursianCP)");
        }

        if (!Files.isDirectory(addressToCopy)) {
            if (!Files.exists(addressToCopy)) {
                try {
                    Files.createFile(addressToCopy);
                } catch (IOException ioexc) {
                    throw new ShellException(addressToCopy + "can't create(doRecursianCP)");
                }
                //throw new ShellException(addressToCopy + " adress is not a direction");
            }
        } else {
            addressToCopy = Paths.get(addressToCopy.toString(), objectToCopy.getFileName().toString());
        }



        //System.out.println(objectToCopy.toString());
        //System.out.println(addressToCopy.toString());

        // if (objectToCopy.equals(addressToCopy))
        // {
        //    addressToCopy = Paths.get(addressToCopy.toString(), "(1)");
        // }

        try {
            Files.copy(objectToCopy, addressToCopy, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exc) {
            throw new ShellException("unsuccessful trying to copy");
        }

    }

    boolean subString(String firstString, String secondString) {
        if (firstString.length() >= secondString.length()) {
            return false;
        }

        for (int i = 0; i < firstString.length(); ++i) {
            if (firstString.charAt(i) != secondString.charAt(i)) {
                return false;
            }
        }

        // if (secondString.charAt(firstString.length()) !=  )
        //     return false;

        return true;
    }

    public void doRM(Vector<String> args) {
        //System.out.println(args.lastElement());


        if (args.size() == 1) {
            throw new ShellException("not enough arguments for rm");
        }

        boolean makeRecursion = false;

        if (args.get(1).equals("-r")) {
            makeRecursion = true;
        }

        if (makeRecursion && args.size() < 3) {
            throw new ShellException("not enough arguments for recursive rm");
        }

        if (makeRecursion && args.size() > 3) {
            throw new ShellException("too much arguments for recursive rm");
        }

        if (!makeRecursion && args.size() < 2) {
            throw new ShellException("not enough arguments for nonrecursive rm");
        }

        if (!makeRecursion && args.size() > 2) {
            throw new ShellException("too much arguments for nonrecursive rm");
        }

        if (makeRecursion) {
            doRecursianRM(args);
        }

        if (!makeRecursion) {
            doNonRecursianRM(args);
        }



    }

    public void doRecursianRM(Vector<String> args) {
        Path objectToDelete = currentDirectory.resolve(args.get(2)).toAbsolutePath();

        objectToDelete.normalize();

        //System.out.println(objectToDelete + " recursive way!");

        if (!Files.exists(objectToDelete)) {
            throw new ShellException("file doesn't exists");
        }

        if (Files.isDirectory(objectToDelete)) {
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(objectToDelete);
                for (Path innerDirectory : stream) {
                    Vector<String> subFolder = new Vector<String>();
                    subFolder.add(args.get(0));
                    subFolder.add(args.get(1));
                    subFolder.add(innerDirectory.toString());
                    doRM(subFolder);
                }

            } catch (IOException exc) {
                throw new ShellException("impossible to open a directory");
            }
        }

        try {
            Files.delete(objectToDelete);
        } catch (IOException exc) {
            throw new ShellException("unsucsesful trying to delete file");
        }
    }

    public void doNonRecursianRM(Vector<String> args) {
        Path objectToDelete = currentDirectory.resolve(args.get(1)).toAbsolutePath();

        objectToDelete.normalize();

        if (!Files.exists(objectToDelete)) {
            throw new ShellException("something wrong with this file");
        }

        try {
            Files.delete(objectToDelete);
        } catch (IOException exc) {
            throw new ShellException("unsucsesful trying to delete file");
        }

    }

    public void doMV(Vector<String> args) {
        if (args.size() < 3) {
            throw new ShellException("not enough arguments for mv");
        }

        Path objectToMove = currentDirectory.resolve(args.get(1)).toAbsolutePath();
        Path destinatinDirectory = currentDirectory.resolve(args.get(2)).toAbsolutePath();

        Vector<String> toCP = new Vector<String>();

        toCP.add("cp");
        toCP.add(objectToMove.toString());
        toCP.add(destinatinDirectory.toString());

        Vector<String> toRM = new Vector<String>();

        toRM.add("rm");
        toRM.add(objectToMove.toString());

        doCP(toCP);
        doRM(toRM);
    }

}
