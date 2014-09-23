package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.*;

public class Mkdir implements ShellCommand {

	String currentPath;
	String FileName;

	public Mkdir(String currentPath, String FileName) {
		this.FileName = FileName;
		this.currentPath = currentPath;
	}

	@Override
	public boolean execute() {

		File f = new File(currentPath + System.getProperty("file.separator")
				+ FileName);
		if (f.mkdir()) {
			System.out.println("Directory " + FileName + " created!");
		} else {
			System.out.println("Couldn't create new directory!");
			return false;
		}
		return true;
	}
}
