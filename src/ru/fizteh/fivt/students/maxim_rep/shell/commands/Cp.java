package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Cp implements ShellCommand {

String currentPath;
String source;
String destination;
	boolean recursive;

	public Cp(String currentPath, String source, String destination,
			boolean recursive) {
		this.currentPath = currentPath;
		this.source = Parser.pathConverter(source, currentPath);
		this.destination = Parser.pathConverter(destination, currentPath);
		this.recursive = recursive;
	}

	public static boolean copyDirectory(String source, String destination) {
		File fileToCopy = new File(source);
		File fileNew = new File(destination);

		if (!fileToCopy.exists()) {
			System.out.println("cp: '" + source
					+ "': No such file or directory!");
			return false;
		}

		if (fileToCopy.isFile()) {
			return copyFile(source, destination);

		}

		if (fileNew.exists()) {
			if (fileNew.isFile()) {
				try {
					fileNew.mkdir();
					copyrecursive(fileToCopy, fileNew);
				} catch (Exception e) {
					System.out.println("cp: Error " + e.getMessage());
					return false;
				}
			} else if (fileNew.isDirectory()) {
				fileNew = new File(destination
						+ System.getProperty("file.separator")
						+ fileToCopy.getName());
			}
			try {
				fileNew.mkdir();
				copyrecursive(fileToCopy, fileNew);
			} catch (Exception e) {
				System.out.println("cp: Error " + e.getMessage());
				return false;
			}
		} else {
			try {
				fileNew.mkdir();
				copyrecursive(fileToCopy, fileNew);
			} catch (Exception e) {
				System.out.println("cp: Error " + e.getMessage());
				return false;
			}
		}
		return true;

	}

	private static void copyrecursive(File fileToCopy, File filedestination) {

		String[] filesInDir = fileToCopy.list();
		for (int i = 0; i < filesInDir.length; ++i) {
			File current = new File(fileToCopy.getAbsoluteFile()
					+ System.getProperty("file.separator") + filesInDir[i]);
			if (current.isFile()) {
				copyFile(
						fileToCopy.getAbsolutePath()
								+ System.getProperty("file.separator")
								+ filesInDir[i],
						filedestination.getAbsolutePath());
			} else {
				File tmp = new File(filedestination.getAbsoluteFile()
						+ System.getProperty("file.separator") + filesInDir[i]);
				tmp.mkdir();

				File push = new File(filedestination.getAbsolutePath()
						+ System.getProperty("file.separator") + filesInDir[i]);
				File ctpush = new File(fileToCopy.getAbsolutePath()
						+ System.getProperty("file.separator") + filesInDir[i]);
				copyrecursive(ctpush, push);
			}
		}
	}

	public static boolean copyFile(String source, String destination) {
		File fileToCopy = new File(source);
		File fileNew = new File(destination);

		if (!fileToCopy.exists()) {
			System.out.println("cp: '" + source
					+ "': No such file or directory!");
			return false;
		}

		if (fileToCopy.isDirectory()) {
			System.out.println("cp: '" + source + "': Is a directory!");
			return false;
		}

		if (fileNew.exists()) {
			if (fileNew.isFile()) {
				try {
					Files.copy(fileToCopy.toPath(), fileNew.toPath());
				} catch (IOException e) {
					System.out.println("cp: Error " + e.getMessage());
					return false;
				}
			} else if (fileNew.isDirectory()) {
				fileNew = new File(destination
						+ System.getProperty("file.separator")
						+ fileToCopy.getName());
			}
			try {
				Files.copy(fileToCopy.toPath(), fileNew.toPath());
			} catch (IOException e) {
				System.out.println("cp: Error " + e.getMessage());
				return false;
			}
		} else {
			try {
				Files.copy(fileToCopy.toPath(), fileNew.toPath());
			} catch (IOException e) {
				System.out.println("cp: Error " + e.getMessage());
				return false;
			}
		}
		return true;

	}

	@Override
	public boolean execute() {
		if (recursive) {
			return copyDirectory(source, destination);
		} else {
			return copyFile(source, destination);
		}
	}
}
