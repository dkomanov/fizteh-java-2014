package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.File;
import java.io.IOException;

public class Cd implements ShellCommand {

	private String CurrentPath;
	private String Destination;

	public Cd(String CurrentPath, String Destination) {
		this.CurrentPath = CurrentPath;
		this.Destination = ru.fizteh.fivt.students.maxim_rep.shell.Parser.PathConverter(Destination,
				this.CurrentPath);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean execute() {
		File f = new File(Destination);

		if (!f.exists()) {
			System.err.println("cd: " + Destination
					+ ": No such file or directory");
			return false;
		} else {
			try {
				ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath = f.getCanonicalPath();
			} catch (IOException e) {
				System.err.println("cd: Error: " + e.getMessage());
				return false;
			}
		}
		return true;
	}

}
