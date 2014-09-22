package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.*;
import java.io.File;
import java.io.IOException;

public class cd implements shellCommand {

	private String CurrentPath;
	private String Destination;

	public cd(String CurrentPath, String Destination) {
		this.CurrentPath = CurrentPath;
		this.Destination = parser.PathConverter(Destination, this.CurrentPath);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean execute() {
		File f = new File(Destination);

		if (!f.exists()) {
			System.out.println("cd: " + Destination
					+ ": No such file or directory");
			return false;
		} else {
			try {
				shell.CurrentPath = f.getCanonicalPath();
			} catch (IOException e) {
				System.out.println("cd: Error: " + e.getMessage());
				return false;
			}
		}
		return true;
	}

}
