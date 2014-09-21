package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by mikhail on 21.09.14.
 */
public class FileVisitorCopy extends SimpleFileVisitor<Path> {
    String destinationPath;

    void setDestinationPath(String newPath) {
        destinationPath = newPath;
    }
    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr){
        //System.out.println(filePath.toString() + " is visited");
        /*try {
            //Files.copy(filePath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("IOException: " + filePath.toAbsolutePath().toString());
        }*/
        return CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(Path filePath, IOException exc){
        //System.out.println(filePath.toAbsolutePath().toString());
        System.err.println("Error occuried while visitтпg file");
        return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes attr){
        try{
            System.out.println(directoryPath.toString() + "\n" + destinationPath);
            Files.createDirectory(Paths.get(destinationPath + directoryPath));
        }
        catch(IOException e){
            System.out.println("IOException " + directoryPath.toAbsolutePath().toString());
        }
        return CONTINUE;
    }
}