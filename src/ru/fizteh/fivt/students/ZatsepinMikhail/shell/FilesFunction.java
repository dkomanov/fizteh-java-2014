package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.Paths;

/**
 * Created by mikhail on 23.09.14.
 */
public class FilesFunction {

    public static String toAbsolutePathString(String myPath){
        String absolutePathString;
        String fileSeparator = System.getProperty("file.separator");
        if (Paths.get(myPath).isAbsolute()) {
            absolutePathString = myPath;
        }
        else{
            absolutePathString = System.getProperty("user.dir") + fileSeparator + myPath;
        }
        absolutePathString = Paths.get(absolutePathString).normalize().toString();
        return absolutePathString;
    }
}
