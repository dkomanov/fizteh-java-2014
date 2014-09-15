package ru.fizteh.fivt.students.vadim_mazaev.shell;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;


public abstract class ShellParser {
	public static boolean parse(final String cmd) {
		String cmdArgs = "";
		String cmdWithoutArgs = cmd.trim();
		int cmdWithoutArgsEnds = cmd.trim().indexOf(" ");
		if (cmdWithoutArgsEnds != -1) {
			cmdArgs = cmd.trim().substring(cmdWithoutArgsEnds + 1);
			cmdWithoutArgs = cmd.trim().substring(0, cmdWithoutArgsEnds);
		}
		
		switch (cmdWithoutArgs) {
		case "exit":
			System.exit(0);
		case "pwd":
			return pwd();
		case "ls":
			return ls();
		case "cd":
			return cd(cmdArgs);
		case "mkdir":
			return mkdir(cmdArgs);
		case "cat":
			return cat(cmdArgs);
		default:
			System.out.println(cmdWithoutArgs + ": no such command");
			return false;
		}
	}
	
	private static boolean pwd() {
		try {
			System.out.println(System.getProperty("user.dir"));
			return true;
		} catch (SecurityException e) {
			System.out.println(
			"pwd: cannot read current working directory: access denied");
		}
		return false;
	}
	
	private static boolean ls() {
		try {
			String[] fileNamesList =
					new File(System.getProperty("user.dir")).list();
			for (int i = 0; i < fileNamesList.length; i++) {
				System.out.println(fileNamesList[i]);
			}
			return true;
		} catch (SecurityException e) {
			System.out.println(
					"ls: cannot get the list of files: access denied");
		}
		return false;
	}
	
	private static boolean cd(final String cmdArgs) {
		if (cmdArgs.isEmpty()) {
			System.out.println("cd: missing operand");
			return false;
		}
		try {
			Path path = FileSystems.getDefault().getPath(cmdArgs);
			if (!path.isAbsolute()) {
				path = FileSystems.getDefault()
						.getPath(System.getProperty("user.dir"), cmdArgs);
			}
			File newWorkingDir = path.normalize().toFile();
			if (newWorkingDir.exists()) {
				System.setProperty("user.dir", newWorkingDir.getPath());
				return true;
			} else {
				System.out.println("cd: '"
					+ newWorkingDir.getPath() + "': No such file or directory");
			}
		} catch (InvalidPathException e) {
			System.out.println(
					"cd: cannot change directory: illegal character in path");
		} catch (SecurityException e) {
			System.out.println("cd: cannot change directory: access denied");
		}
		return false;
	}
	
	private static boolean mkdir(final String cmdArgs) {
		if (cmdArgs.isEmpty()) {
			System.out.println("mkdir: missing operand");
			return false;
		}
		try {	
			File makedDir = FileSystems.getDefault()
					.getPath(System.getProperty("user.dir"), cmdArgs).toFile();
			if (!makedDir.mkdir()) {
				System.out.println(
						"mkdir: cannot create directory '"
								+ cmdArgs + "': File exists");
				return false;
			}
			return true;
		} catch (InvalidPathException e) {
			System.out.println(
					"mkdir: cannot create directory '"
						+ cmdArgs + "': illegal character in name");
		} catch (SecurityException e) {
			System.out.println(
					"mkdir: cannot create directory '"
						+ cmdArgs + "': access denied");
		}
		return false;
	}
	
	private static boolean cat(final String cmdArgs) {
		if (cmdArgs.isEmpty()) {
			System.out.println("cat: missing operand");
			return false;
		}
		try {
			File cattedFile = FileSystems.getDefault()
					.getPath(System.getProperty("user.dir"), cmdArgs).toFile();
			if (cattedFile.exists()) {
				if (cattedFile.isFile()) {
					try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(cattedFile)));) {
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
						//System.out.println();
					}
					catch (IOException e) {
						System.out.println(
								"cat " + cmdArgs + ": cannot read file");
						return false;
					}
					return true;
				} else {
					System.out.println(
						"cat: " + cmdArgs + ": This is a directory");
				}
			} else {
				System.out.println(
					"cat: " + cmdArgs + ": No such file or directory");
			}
		} catch (SecurityException e) {
			System.out.println(
				"cat: cannot open file '" + cmdArgs + "': access denied");
		} catch (InvalidPathException e) {
			System.out.println(
				"cat: cannot open file '"
					+ cmdArgs + "': illegal character in name");
		}
		return false;
	}
}
