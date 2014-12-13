package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ThreadInterruptException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static String concatStrings(String[] args, String separator) {
        StringBuilder sb = new StringBuilder();
        if (args.length == 0) {
            return "";
        }
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (args[i] != null) {
                sb.append(separator).append(args[i]);
            }
        }
        return sb.toString();
    }

    public static String currentPath() {
        return System.getProperty("user.dir");
    }

    public static Path makePathAbsolute(String str) {
        Path path = Paths.get(str);
        if (!path.isAbsolute()) {
            path = Paths.get(Utils.currentPath(), path.toString());
        }
        return path.normalize();
    }

    public static Path makePathAbsolute(Path mainPath, String str) {
        Path path = Paths.get(str);
        if (!path.isAbsolute()) {
            path = Paths.get(mainPath.toString(), path.toString());
        }
        return path.normalize();
    }

    public static Path makePathAbsolute(File mainPath, String str) {
        Path path = Paths.get(str);
        if (!path.isAbsolute()) {
            path = Paths.get(mainPath.toString(), path.toString());
        }
        return path.normalize();
    }

    public static Path toPath(String str) {
        return Paths.get(str).toFile().toPath();
    }

    public static File toFile(String str) {
        return Paths.get(str).toFile();
    }



    public static void delete(File file) throws ThreadInterruptException {
        if (!file.delete()) {
            throw new ThreadInterruptException(file.toString() + ": Can't delete");
        }
    }

    public static void rmdirs(File removed) {
        if (removed.isDirectory()) {
            for (File object: removed.listFiles()) {
                rmdirs(object);
            }
        }
        Utils.delete(removed);
    }



    public static void checkNotNull(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Null argument");
        }
    }

    public static boolean checkNoChanges(InterpreterStateDatabase state) {
        if (state.getTable() == null || state.getTable().getNumberOfUncommittedChanges() == 0) {
            return true;
        }
        state.out.println(state.getTable().getNumberOfUncommittedChanges() + " unsaved changes");
        return false;
    }

    public static boolean checkTableChosen(InterpreterStateDatabase state) {
        if (state.getTable() == null) {
            System.out.println("choose a table");
            return false;
        }
        return true;
    }
}



