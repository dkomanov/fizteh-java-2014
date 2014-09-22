package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.*;

public class Rm implements ShellCommand {

	String CurrentPath;
	String Destination;
	boolean Recursive;

	public Rm(String CurrentPath, String Destination, boolean Recursive) {
		this.CurrentPath = CurrentPath;
		this.Destination = ru.fizteh.fivt.students.maxim_rep.shell.Parser.PathConverter(Destination, CurrentPath);
		this.Recursive = Recursive;
	}

	public static void recursiveRm(File f, String Path) {
		String[] files = f.list();
		for (int i = 0; i < files.length; ++i) {
			File currentFile = new File(f.getPath() + "/" + files[i]);
			if (currentFile.isDirectory()) {
				recursiveRm(currentFile, currentFile.getPath());
			}
			currentFile.delete();
		}

		f.delete();
	}

	@Override
	public boolean execute() {
		File f = new File(Destination);
		if (f.isFile()) {
			f.delete();
			return true;
		} else if (f.isDirectory()) {
			if (Recursive) {
				recursiveRm(f, Destination);
			} else {
				f.delete();
			}
		} else {
			System.err.println("rm: cannot remove '" + Destination
					+ "': No such file or directory");
			return false;
		}
		return true;
	}
}
