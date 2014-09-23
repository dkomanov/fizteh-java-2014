package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by mikhail on 21.09.14.
 */
public class CommandCp extends Command {
    public CommandCp() {
        name = "cp";
        numberOfArguments = 4;
    }

    @Override
    public boolean run(String[] arguments) {
        if (arguments.length != 4 & arguments.length != 3)
            return false;
        boolean recursive = (arguments.length == numberOfArguments & arguments[1].equals("-r"));
        if (!recursive)
            return generalCopy(arguments);
        else
            return recursiveCopy(arguments);
    }

    private boolean generalCopy(String[] arguments) {
        String startPath = FilesFunction.toAbsolutePathString(arguments[1]);
        String destinationPath = FilesFunction.toAbsolutePathString(arguments[2]);
        String[] parsedFileName = arguments[1].split("/");
        String fileName = parsedFileName[parsedFileName.length - 1];

        if (Files.isDirectory(Paths.get(startPath))){
            System.out.println(name + ": omitting directory \'" + arguments[1] + "\'");
            return false;
        }

        if (!Files.exists(Paths.get(startPath))) {
            System.out.println(name + ": cannot copy \'" + arguments[1] + "\'" +
                    ": No such file or directory");
            return false;
        }

        if (Files.isDirectory(Paths.get(destinationPath))){
            destinationPath = destinationPath + System.getProperty("file.separator") + fileName;
        }
        else if (destinationPath.charAt(destinationPath.length() - 1) == '/') {
            System.out.println(name + ": cannot copy \'" + fileName + "\' to \'" +
                    arguments[2] + "\': Not a directory");
            return false;
        }

        try {
            Files.copy(Paths.get(startPath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            System.out.println(name + "; cannot create regular file \'" + arguments[2] + "\'" +
                        ": Not a directory");
            return false;
        }
        return true;
    }

    private boolean recursiveCopy(String[] arguments) {
        Path startPath = Paths.get(FilesFunction.toAbsolutePathString(arguments[2]));
        Path destinationPath = Paths.get(FilesFunction.toAbsolutePathString(arguments[3]));

        destinationPath = destinationPath.resolve(startPath.getFileName()).normalize();

        System.out.println(destinationPath.toString());
        FileVisitorCopy myFileVisitorCopy;
        myFileVisitorCopy = new FileVisitorCopy(startPath, destinationPath);

        if (myFileVisitorCopy != null){
            try {
                Files.createDirectory(destinationPath);
                Files.walkFileTree(startPath, myFileVisitorCopy);
            }
            catch(IOException e) {
                System.out.println("Error while walkingFileTree");
                return false;
            }
        }
        return true;
    }
}