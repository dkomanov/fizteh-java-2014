package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.shell;
import java.io.File;

public class ls implements shellCommand {

	private String CurrentPath;

	public ls(String CurrentPath) {
		this.CurrentPath = CurrentPath;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean execute() {
		File f = new File(CurrentPath);
		if (!f.exists()) {
			System.err
					.println("Current directory doesn't exists, returning to root.");
			shell.CurrentPath = "/";
			return false;
		} else {
			String[] filelist = f.list();
			for (String filelist1 : filelist) {
				System.out.println(filelist1);
			}
		}
		return true;
	}

}
