package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by astepanov on 20.09.14.
 */
public class ChangeDirectoryAction extends Action {
    public ChangeDirectoryAction(String[] args) {
        super(args);
    }

    @Override
    public boolean run() {
		if (arguments.length != 2) {
			System.err.println("cd: wrong number of arguments");
			return false;
		}
        String path = arguments[1];
        File f = FileUtils.fromPath(path);

        if (!f.isDirectory()) {
            System.err.printf("cd: '%s': No such file or directory\n", path);
            return false;
        }
        try {
            System.setProperty("user.dir", f.getCanonicalPath());
        } catch (IOException e) {
            System.err.printf("cd: '%s': unable to read file\n", path);
            return false;
        }
        return true;
    }
}
