package ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static void interpreterError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public static String[] findAll(String regexp, String text) {
        Matcher matcher = Pattern.compile(regexp).matcher(text);
        List<String> occurrences = new ArrayList<>();
        while (matcher.find()) {
            occurrences.add(matcher.group());
        }
        return occurrences.toArray(new String[occurrences.size()]);
    }

    public static void checkOnNull(String name, String msg) {
        if (name == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void  recursiveDelete(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                            /*
                             * Directory iteration failed.
                              */
                        throw e;
                    }
                }
            });
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
