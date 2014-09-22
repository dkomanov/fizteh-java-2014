package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.Parser;
import java.io.*;

public class Run implements ShellCommand {

	String currentPath;
	String destination;
	boolean recursive;

	public Run(String currentPath, String destination, boolean recursive) {
		this.currentPath = currentPath;
		this.destination = Parser.pathConverter(destination, currentPath);
		this.recursive = recursive;
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
		File f = new File(destination);
		if (f.isFile()) {
			f.delete();
			return true;
		} else if (f.isDirectory()) {
			if (recursive) {
				recursiveRm(f, destination);
			} else {
				f.delete();
			}
		} else {
			System.out.println("rm: cannot remove '" + destination
					+ "': No such file or directory");
			return false;
		}
		return true;
	}
}
