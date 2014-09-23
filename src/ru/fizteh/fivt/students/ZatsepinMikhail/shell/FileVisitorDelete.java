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
public class FileVisitorDelete extends SimpleFileVisitor<Path>{
    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr){
        try {
            Files.delete(filePath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("IOException: " + filePath.toAbsolutePath().toString());
        }
        return CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(Path filePath, IOException exc){
        System.err.println("Error occuried while visitтпg file");
        return CONTINUE;
    }
    @Override
    public FileVisitResult postVisitDirectory(Path directoryPath, IOException exc){
        try{
            Files.delete(directoryPath.toAbsolutePath());
        }
        catch(IOException e){
            System.out.println("IOException " + directoryPath.toAbsolutePath().toString());
        }
        return CONTINUE;
    }
}