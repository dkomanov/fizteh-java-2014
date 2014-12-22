package ru.fizteh.fivt.students.Volodin_Denis.JUnit.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static void createDirectory(final String path) throws IOException {
        Files.createDirectory(Paths.get(path));
    }

    public static boolean createDirectory(final String path, final String secondPath) {
        return Paths.get(path, secondPath).normalize().toFile().mkdir();
    }

    public static boolean delete(final String path, final String secondPath) {
        return Paths.get(path, secondPath).normalize().toFile().delete();
    }
    
    public static boolean exists(final String path) {
        return Paths.get(path).normalize().toFile().exists();
    }
    
    public static boolean exists(final String path, final String secondPath) {
        return Paths.get(path, secondPath).normalize().toFile().exists();
    }
    
    public static Path get(final String path) {
        return Paths.get(path).toAbsolutePath().normalize();
    }
    
    public static Path get(final String path, final String secondPath) {
        return Paths.get(path, secondPath).toAbsolutePath().normalize();
    }
    
    public static String getFileName(final String path) {
        return Paths.get(path).toAbsolutePath().normalize().getFileName().toString();
    }

    public static String getFileName(final String path, final String secondPath) {
        return Paths.get(path, secondPath).normalize().getFileName().toString();
    }
    
    public static String getParentName(final String path) {
        return Paths.get(path).normalize().getParent().getFileName().toString();
    }

    public static String getParentName(final String path, final String secondPath) {
        return Paths.get(path, secondPath).normalize().getParent().getFileName().toString();
    }

    public static boolean isDirectory(final String path) {
        return Paths.get(path).toAbsolutePath().normalize().toFile().isDirectory();
    }
    
    public static boolean isDirectory(final String path, final String secondPath) {
        return Paths.get(path, secondPath).toAbsolutePath().normalize().toFile().isDirectory();
    }
    
    public static String toAbsolutePath(final String path) {
        return Paths.get(path).toAbsolutePath().normalize().toString();
    }
}
