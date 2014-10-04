package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by astepanov on 21.09.14.
 */
public class CopyAction extends Action {

    public CopyAction(String[] args) {
        super(args);
    }

    private boolean copyRecursively(File source, File dest) {
        if (!source.isDirectory()) {
            try {
                if (!dest.mkdirs()) {
                    return false;
                }
                Files.copy(source.toPath(), dest.toPath().resolve(source.getName()));
            } catch (IOException exception) {
                return false;
            }
            return true;
        }
        for (File sub : source.listFiles()) {
            if (!copyRecursively(sub, new File(dest, sub.getName()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean run() {
		if (arguments.length != 3 && arguments.length != 4) {
			System.err.println("cp: wrong number of arguments");
			return false;
		}
        File source;
        File dest;
        if (arguments[1].equals("-r")) {
            source = FileUtils.fromPath(arguments[2]);
            dest = FileUtils.fromPath(arguments[3]);
        } else {
            source = FileUtils.fromPath(arguments[1]);
            dest = FileUtils.fromPath(arguments[2]);
            if (source.isDirectory()) {
                System.err.printf("cp: %s is a directory (not copied.\n", arguments[1]);
                return false;
            }
        }
        if (!copyRecursively(source, dest)) {
            System.err.printf("cp %s %s: error copying file.\n", source.getPath(), dest.getPath());
            return false;
        }
        return true;
    }
}
