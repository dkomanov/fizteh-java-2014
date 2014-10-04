package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;

/**
 * Created by astepanov on 20.09.14.
 */
public class MakeDirectoryAction extends Action {

    public MakeDirectoryAction(String[] args) {
        super(args);
    }

    @Override
    public boolean run() {
		if (arguments.length != 2) {
			System.err.println("mkdir: wrong number of arguments");
			return false;
		}
        String name = arguments[1];
        File f = FileUtils.fromPath(name);
        if (!f.mkdirs()) {
            System.err.printf("mkdir: '%s': failed to create directory\n", name);
            return false;
        }
        return true;
    }
}
