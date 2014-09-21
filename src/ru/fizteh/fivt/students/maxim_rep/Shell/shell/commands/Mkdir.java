package shell.commands;

import java.io.*;

public class Mkdir implements ShellCommand {

	String CurrentPath;
	String FileName;

	public Mkdir(String CurrentPath, String FileName) {
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
		}
		return true;
	}
}
