package ru.fizteh.fivt.students.Soshilov.FileMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 09 October 2014
 * Time: 21:37
 */
public class Main {
    /**
     * Function, running the whole program.
     * @param currentArgs Commands that were entered: name, its' arguments.
     */
    public static void main(final String[] currentArgs) {
        //System.setProperty("db.file",
        //        "/home/soshikan/IdeaProjects/FileMap/src/ru/fizteh/fivt/students/Soshilov/FileMap/db.file");
        FileMapRun.fileMapRun(currentArgs);
    }
}
