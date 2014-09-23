package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by mikhail on 21.09.14.
 */
public class FileVisitorCopy extends SimpleFileVisitor<Path> {
    private final Path startPath;
    private final Path destinationPath;
    public FileVisitorCopy(Path newStartPath, Path newDestinationPath){
        startPath = newStartPath;
        destinationPath = newDestinationPath;
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr){
        Path newFilePath = destinationPath.resolve(startPath.relativize(filePath));
        try {
            Files.copy(filePath, newFilePath);
        } catch (IOException e) {
            System.out.println("IOException: " + filePath.toAbsolutePath());
        }
        return CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(Path filePath, IOException exc){
        System.err.println("Error occuried while visitтпg file");
        return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attr){
        System.out.println(dirPath.toString() + " - pre");
        Path newDirPath = destinationPath.resolve(startPath.relativize(dirPath));
        try{
            Files.createDirectory(newDirPath);
        }
        catch(IOException e){
            System.out.println("IOException " + newDirPath.toString());
        }
        return CONTINUE;
    }

    /*public FileVisitResult postVisitDirectory(Path directoryPath, BasicFileAttributes attr){
        System.out.println(directoryPath.toString() + " - post");
        destinationPath = Paths.get(destinationPath).getParent().toString();
        try{
            System.out.println(destinationPath + " - creating directory (Before)");
            Files.createDirectory(Paths.get(destinationPath));
            System.out.println(destinationPath + " - creating directory (After)");
        }
        catch(IOException e){
            System.out.println("IOException " + directoryPath.toAbsolutePath().toString());
        }
        return CONTINUE;
    }*/
}