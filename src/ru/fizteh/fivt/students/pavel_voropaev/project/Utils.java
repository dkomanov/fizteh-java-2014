package ru.fizteh.fivt.students.pavel_voropaev.project;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static void rm(Path directory) throws IllegalStateException, IOException {
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    rm(entry);
                }
            }
        }
        if (!directory.toFile().delete()) {
            throw new IOException("Cannot delete " + directory.toString());
        }
    }

    public static String[] findAll(String regexp, String text) {
        Matcher matcher = Pattern.compile(regexp).matcher(text);
        List<String> occurrences = new ArrayList<>();
        while (matcher.find()) {
            occurrences.add(matcher.group());
        }
        return occurrences.toArray(new String[occurrences.size()]);
    }
}
