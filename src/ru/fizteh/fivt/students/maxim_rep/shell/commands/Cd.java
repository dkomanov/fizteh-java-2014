package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.*;
import java.io.File;
import java.io.IOException;

public class Cd implements ShellCommand {

String currentPath;
String destination;

	public Cd(String currentPath, String destination) {
		this.currentPath = currentPath;
		this.destination = Parser.pathConverter(destination, this.currentPath);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean execute() {
		File f = new File(destination);

		if (!f.exists()) {
			System.out.println("cd: " + destination
					+ ": No such file or directory");
			return false;
		} else {
			try {
				Shell.currentPath = f.getCanonicalPath();
			} catch (IOException e) {
				System.out.println("cd: Error: " + e.getMessage());
				return false;
			}
		}
		return true;
	}

}
