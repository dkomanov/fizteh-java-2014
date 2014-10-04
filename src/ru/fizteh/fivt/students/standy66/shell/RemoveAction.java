package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;

/**
 * Created by astepanov on 20.09.14.
 */
public class RemoveAction extends Action {
    public RemoveAction(String[] args) {
        super(args);
    }

    private boolean deleteRecursively(File f) {
        if (f.isDirectory()) {
            for (File sub : f.listFiles()) {
                if (sub.isDirectory()) {
                    if (!deleteRecursively(sub)) {
                        return false;
                    }
                } else if (!sub.delete()) {
                    return false;
                }
            }
        }
        return f.delete();
    }

    @Override
    public boolean run() {
        File f;
        if (arguments[1].equals("-r")) {
            f = FileUtils.fromPath(arguments[2]);
        } else {
            f = FileUtils.fromPath(arguments[1]);
            if (f.isDirectory()) {
                System.err.printf("rm: '%s': is a directory", f.getPath());
                return false;
            }
        }
        if (!deleteRecursively(f)) {
            System.err.printf("rm: '%s': error deleting a file", f.getPath());
            return false;
        }
        return true;
    }
}
