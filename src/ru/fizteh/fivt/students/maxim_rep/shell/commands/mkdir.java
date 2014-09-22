package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.*;

public class mkdir implements shellCommand {

	String CurrentPath;
	String FileName;

	public mkdir(String CurrentPath, String FileName) {
		this.FileName = FileName;
		this.CurrentPath = CurrentPath;
	}

	@Override
	public boolean execute() {

		File f = new File(CurrentPath + "/" + FileName);
		if (f.mkdir()) {
			System.out.println("Directory " + FileName + " created!");
		} else {
			System.out.println("Couldn't create new directory!");
			return false;
		}
		return true;
	}
}
