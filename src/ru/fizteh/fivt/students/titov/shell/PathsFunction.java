package ru.fizteh.fivt.students.titov.shell;

import java.nio.file.Paths;
import java.nio.file.Path;

public class PathsFunction {

    public static Path toAbsolutePathString(final String myPath) {
        String absolutePathString;
        String fileSeparator = System.getProperty("file.separator");
        if (Paths.get(myPath).isAbsolute()) {
            absolutePathString = myPath;
        } else {
            absolutePathString = System.getProperty("user.dir")
                                 + fileSeparator + myPath;
        }
        absolutePathString = Paths.get(absolutePathString).normalize().toString();
        return Paths.get(absolutePathString);
    }
}
