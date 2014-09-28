package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.File;

public class Ls implements ShellCommand {

	String currentPath;

	public Ls(String currentPath) {
		this.currentPath = currentPath;
	}

	@Override
	public boolean execute() {
		File f = new File(currentPath);
		if (!f.exists()) {
			System.err.println("Current directory doesn't exists.");
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
