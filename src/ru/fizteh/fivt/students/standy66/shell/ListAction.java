package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;

/**
 * Created by astepanov on 21.09.14.
 */
public class ListAction extends Action {
	public ListAction(String[] args) {
		super(args);
	}

	@Override
	public boolean run() {
		String path = System.getProperty("user.dir");
		File f = FileUtils.fromPath(path);
		for (File sub : f.listFiles()) {
			System.out.println(sub.getName());
		}
		return true;
	}
}
