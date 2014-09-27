package ru.fizteh.fivt.students.standy66.shell;

/**
 * Created by astepanov on 20.09.14.
 */
public class PrintWorkingDirectoryAction extends Action {
	public PrintWorkingDirectoryAction(String[] args) {
		super(args);
	}

	@Override
	public boolean run() {
		if (arguments.length > 1) {
			System.err.println("pwd must be called with no arguments");
			return false;
		}
		System.out.println(System.getProperty("user.dir"));
		return true;
	}
}
