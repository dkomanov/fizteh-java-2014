package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 21.09.14.
 */
public class CommandCp extends Command {
    public CommandCp() {
        name = "cp";
        numberOfArguments = 4;
    }

    public boolean run(String[] arguments) {
        if (arguments.length != 4 & arguments.length != 3)
            return false;
        boolean recursive = (arguments.length == numberOfArguments & arguments[1].equals("-r"));
        if (!recursive)
            generalCopy(arguments);
        else
            recursiveCopy(arguments);
        return true;
    }

    private boolean generalCopy(String[] arguments) {
        String filePath = arguments[1];
        String destinationPath = arguments[2];
        String[] parsedFileName = arguments[1].split("/");
        String fileName = parsedFileName[parsedFileName.length - 1];
        if (filePath.charAt(0) != '/')
            filePath = System.getProperty("user.dir") + "/" + filePath;
        if (destinationPath.charAt(0) != '/')
            destinationPath = System.getProperty("user.dir") + "/" + destinationPath;
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println(filePath);
            System.out.println(name + ": cannot copy \'" + arguments[1] + "\'" +
                    ": No such file or directory");
            return false;
        }

        if (Files.isDirectory(Paths.get(destinationPath)))
            destinationPath = destinationPath + "/" + fileName;
        else if (destinationPath.charAt(destinationPath.length() - 1) == '/') {
            System.out.println(name + ": cannot copy \'" + fileName + "\' to \'" +
                    arguments[2] + "\': Not a directory");
            return false;
        }
        try {
            System.out.println(filePath + "\n" + destinationPath);
            Files.copy(Paths.get(filePath), Paths.get(destinationPath));
        } catch (Exception e) {
            if (!Files.exists(Paths.get(destinationPath)) &
                    arguments[2].charAt(arguments[2].length() - 1) == '/') {
                System.out.println(name + "; cannot create regular file \'" + arguments[2] + "\'" +
                        ": Not a directory");
            } else
                System.out.println("IOException");
            return false;
        }
        return true;
    }

    private boolean recursiveCopy(String[] arguments) {
        String filePath = arguments[2];
        String destinationPath = arguments[3];
        if (arguments[2].charAt(0) != '/')
            filePath = System.getProperty("user.dir") + "/" + filePath;
        if (arguments[3].charAt(0) != '/')
            destinationPath = System.getProperty("user.dir") + "/" + destinationPath;

        FileVisitorCopy myFileVisitorCopy;
        myFileVisitorCopy = new FileVisitorCopy();
        myFileVisitorCopy.setDestinationPath(destinationPath);
        if (myFileVisitorCopy != null){
            try {
                Files.walkFileTree(Paths.get(filePath), myFileVisitorCopy);
            }
            catch(IOException e) {
                System.out.println("Error while walkingFileTree");
                return false;
            }
        }
        return true;
    }
}