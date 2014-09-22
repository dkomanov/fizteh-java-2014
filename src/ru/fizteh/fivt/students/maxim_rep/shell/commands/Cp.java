package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Cp implements ShellCommand {

	String CurrentPath;
	String Source;
	String Destination;
	boolean Recursive;

	public Cp(String CurrentPath, String Source, String Destination,
			boolean Recursive) {
		this.CurrentPath = CurrentPath;
		this.Source = ru.fizteh.fivt.students.maxim_rep.shell.Parser.PathConverter(Source, CurrentPath);
		this.Destination = ru.fizteh.fivt.students.maxim_rep.shell.Parser.PathConverter(Destination, CurrentPath);
		this.Recursive = Recursive;
	}

	public static boolean copyDirectory(String Source, String Destination) {
		File fileToCopy = new File(Source);
		File fileNew = new File(Destination);

		if (!fileToCopy.exists()) {
			System.err.println("cp: '" + Source
					+ "': No such file or directory!");
			return false;
		}

		if (fileToCopy.isFile()) {
			return copyFile(Source, Destination);

		}

		if (fileNew.exists()) {
			if (fileNew.isFile())
				try {
					fileNew.mkdir();
					copyRecursive(fileToCopy, fileNew);
				} catch (Exception e) {
					System.err.println("cp: Error " + e.getMessage());
					return false;
				}
			else if (fileNew.isDirectory())
				fileNew = new File(Destination + "/" + fileToCopy.getName());
			try {
				fileNew.mkdir();
				copyRecursive(fileToCopy, fileNew);
			} catch (Exception e) {
				System.err.println("cp: Error " + e.getMessage());
				return false;
			}
		} else {
			try {
				fileNew.mkdir();
				copyRecursive(fileToCopy, fileNew);
			} catch (Exception e) {
				System.err.println("cp: Error " + e.getMessage());
				return false;
			}
		}
		return true;

	}

	private static void copyRecursive(File fileToCopy, File fileDestination) {

		String[] filesInDir = fileToCopy.list();
		for (int i = 0; i < filesInDir.length; ++i) {
			File current = new File(fileToCopy.getAbsoluteFile() + "/"
					+ filesInDir[i]);
			if (current.isFile()) {
				copyFile(fileToCopy.getAbsolutePath() + "/" + filesInDir[i],
						fileDestination.getAbsolutePath());
			} else {
				File tmp = new File(fileDestination.getAbsoluteFile() + "/"
						+ filesInDir[i]);
				tmp.mkdir();

				File push = new File(fileDestination.getAbsolutePath() + "/"
						+ filesInDir[i]);
				File ctpush = new File(fileToCopy.getAbsolutePath() + "/"
						+ filesInDir[i]);
				copyRecursive(ctpush, push);
			}
		}
	}

	public static boolean copyFile(String Source, String Destination) {
		File fileToCopy = new File(Source);
		File fileNew = new File(Destination);

		if (!fileToCopy.exists()) {
			System.err.println("cp: '" + Source
					+ "': No such file or directory!");
			return false;
		}

		if (fileToCopy.isDirectory()) {
			System.err.println("cp: '" + Source + "': Is a directory!");
			return false;
		}

		if (fileNew.exists()) {
			if (fileNew.isFile())
				try {
					Files.copy(fileToCopy.toPath(), fileNew.toPath());
				} catch (IOException e) {
					System.err.println("cp: Error " + e.getMessage());
					return false;
				}
			else if (fileNew.isDirectory())
				fileNew = new File(Destination + "/" + fileToCopy.getName());
			try {
				Files.copy(fileToCopy.toPath(), fileNew.toPath());
			} catch (IOException e) {
				System.err.println("cp: Error " + e.getMessage());
				return false;
			}
		} else {
			try {
				Files.copy(fileToCopy.toPath(), fileNew.toPath());
			} catch (IOException e) {
				System.err.println("cp: Error " + e.getMessage());
				return false;
			}
		}
		return true;

	}

	@Override
	public boolean execute() {
		if (Recursive)
			return copyDirectory(Source, Destination);
		else
			return copyFile(Source, Destination);
	}
}
