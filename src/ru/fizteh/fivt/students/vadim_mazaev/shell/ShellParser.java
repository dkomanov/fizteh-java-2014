package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public abstract class ShellParser {
	public static boolean parse(final String[] cmdWithArgs) {
		switch (cmdWithArgs[0]) {
		case "exit":
			System.exit(0);
		case "pwd":
			if (cmdWithArgs.length > 1) {
				System.out.println("pwd: two much arguments");
				return false;
			}
			return pwd();
		case "ls":
			if (cmdWithArgs.length > 1) {
				System.out.println("ls: two much arguments");
				return false;
			}
			return ls();
		case "cd":
			return cd(cmdWithArgs);
		case "mkdir":
			return mkdir(cmdWithArgs);
		case "cat":
			return cat(cmdWithArgs);
		case "rm":
			return rm(cmdWithArgs);
		case "cp":
			return cp(cmdWithArgs);
		default:
			System.out.println(cmdWithArgs[0] + ": no such command");
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
			for (String fileName : fileNamesList) {
				System.out.println(fileName);
			}
			return true;
		} catch (SecurityException e) {
			System.out.println(
					"ls: cannot get the list of files: access denied");
		}
		return false;
	}
	
	private static boolean cd(final String[] cmd) {
		if (cmd.length == 1) {
			System.out.println("cd: missing operand");
			return false;
		} else if (cmd.length > 2) {
			System.out.println("cd: two much arguments");
			return false;
		}
		try {
			File newWorkingDir = Paths.get(cmd[1]).normalize().toFile();
			if (!newWorkingDir.isAbsolute()) {
				newWorkingDir = Paths.get(
						System.getProperty("user.dir"), cmd[1])
							.normalize().toFile();
			}
			if (newWorkingDir.exists()) {
				if (newWorkingDir.isDirectory()) {
					System.setProperty("user.dir", newWorkingDir.getPath());
					return true;
				} else {
					System.out.println("cd: '"
						+ newWorkingDir.getPath()
							+ "': This is not a directory");
				}
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
	
	private static boolean mkdir(final String[] cmd) {
		if (cmd.length == 1) {
			System.out.println("mkdir: missing operand");
			return false;
		} else if (cmd.length > 2) {
			System.out.println("mkdir: two much arguments");
			return false;
		}
		try {	
			File makedDir = Paths.get(
					System.getProperty("user.dir"), cmd[1]).toFile();
			if (!makedDir.mkdir()) {
				System.out.println(
						"mkdir: cannot create directory '"
								+ cmd[1] + "': File exists");
				return false;
			}
			return true;
		} catch (InvalidPathException e) {
			System.out.println(
					"mkdir: cannot create directory '"
						+ cmd[1] + "': illegal character in name");
		} catch (SecurityException e) {
			System.out.println(
					"mkdir: cannot create directory '"
						+ cmd[1] + "': access denied");
		}
		return false;
	}
	
	private static boolean cat(final String[] cmd) {
		if (cmd.length == 1) {
			System.out.println("cat: missing operand");
			return false;
		} else if (cmd.length > 2) {
			System.out.println("cat: two much arguments");
			return false;
		}
		try {
			File cattedFile = Paths.get(
					System.getProperty("user.dir"), cmd[1]).toFile();
			if (cattedFile.exists()) {
				if (cattedFile.isFile()) {
					try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(cattedFile)));) {
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
					}
					catch (IOException e) {
						System.out.println(
								"cat " + cmd[1] + ": cannot read file");
						return false;
					}
					return true;
				} else {
					System.out.println(
						"cat: " + cmd[1] + ": This is a directory");
				}
			} else {
				System.out.println(
					"cat: " + cmd[1] + ": No such file or directory");
			}
		} catch (SecurityException e) {
			System.out.println(
				"cat: cannot open file '" + cmd[1] + "': access denied");
		} catch (InvalidPathException e) {
			System.out.println(
				"cat: cannot open file '"
					+ cmd[1] + "': illegal character in name");
		}
		return false;
	}
	
	private static boolean rm(final String[] cmd) {
		int keyIndex = 0;
		if (cmd[1] == "-r") {
			keyIndex = 1;
		}
		if (cmd.length == 1 || (cmd.length == 2 && cmd[1] == "-r")) {
			System.out.println("rm: missing operand");
			return false;
		} else if (cmd.length > 2 || (cmd.length > 3 && cmd[1] != "-r")) {
			System.out.println("rm: two much arguments");
			return false;
		}
		try {
			File removedFile = Paths.get(cmd[keyIndex + 1])
					.normalize().toFile();
			if (!removedFile.isAbsolute()) {
				removedFile = Paths.get(System.getProperty("user.dir"),
					cmd[keyIndex + 1]).normalize().toFile();
			}
			if (!removedFile.exists()) {
				System.out.println("rm: "
					+ cmd[keyIndex + 1] + ": No such file or directory");
				return false;
			}
			if (removedFile.isFile()) {
				if (!removedFile.delete()) {
					System.out.println("rm: " + cmd[keyIndex + 1]
							+ ": cannot delete file");
					return false;
				}
				return true;
			} else {
				if (keyIndex == 0) {
					System.out.println("rm: " + cmd[keyIndex + 1]
							+ ": is a directory");
					return false;
				}
				if (!rmRec(removedFile)) {
					System.out.println("rm: " + cmd[keyIndex + 1]
							+ ": cannot delete file");
					return false;
				}
			}
		} catch (InvalidPathException e) {
			System.out.println(
					"rm: cannot delete file '"
						+ cmd[keyIndex + 1] + "': illegal character in name");
		} catch (SecurityException e) {
			System.out.println(
					"rm: cannot delete file '"
						+ cmd[keyIndex + 1] + "': access denied");
		}
		return false;
	}
	
	private static boolean rmRec(final File removed) {
		if (removed.isDirectory()) {
			for (File f : removed.listFiles()) {
				rmRec(f);
			}
		}
		return removed.delete();
	}
	
	private static boolean cp(final String[] cmd) {
		int keyIndex = 0;
		if (cmd[1] == "-r") {
			keyIndex = 1;
		}
		if (cmd.length <= 2 || (cmd.length <= 3 && cmd[1] == "-r")) {
			System.out.println("cp: missing operand");
			return false;
		} else if (cmd.length > 3 || (cmd.length > 4 && cmd[1] != "-r")) {
			System.out.println("cp: two much arguments");
			return false;
		}
		try {
			File copiedFile = Paths.get(cmd[keyIndex + 1])
					.normalize().toFile();
			if (!copiedFile.isAbsolute()) {
				copiedFile = Paths.get(System.getProperty("user.dir"),
					cmd[keyIndex + 1]).normalize().toFile();
			}
			if (!copiedFile.exists()) {
				System.out.println("cp: "
					+ cmd[keyIndex + 1] + ": No such file or directory");
				return false;
			}
			try {
				File destinationFile = Paths.get(cmd[keyIndex + 1])
						.normalize().toFile();
				if (!destinationFile.isAbsolute()) {
					destinationFile = Paths
							.get(System.getProperty("user.dir"),
								cmd[keyIndex + 1]).normalize().toFile();
				}
				if (copiedFile.isFile()) {
					destinationFile.createNewFile();
				} else {
					if (keyIndex == 0) {
						System.out.println("cp: " + cmd[keyIndex + 1]
								+ ": is a directory");
						return false;
					}
					destinationFile.mkdir();
				}
				if (!cpRec(copiedFile, destinationFile)) {
					System.out.println(
							"cp: cannot copy file '"
								+ cmd[keyIndex + 1] + "' to file '"
								+ cmd[keyIndex + 2] + "'");
				}
				return true;
			} catch (IOException e) {
				System.out.println(
						"cp: cannot create file '"
							+ cmd[keyIndex + 2] + "': access denied");
			}
		} catch (InvalidPathException e) {
			System.out.println(
					"cp: cannot copy file '"
						+ cmd[keyIndex + 1] + "' to file '"
						+ cmd[keyIndex + 2] + "': illegal character in name");
		} catch (SecurityException e) {
			System.out.println(
					"cp: cannot copy file '"
						+ cmd[keyIndex + 1] + "' to file '"
						+ cmd[keyIndex + 2] + "': access denied");
		}
		return false;
	}
	
	private static boolean cpRec(final File copied, final File destination) {
		if (copied.isDirectory()) {
			for (File f : copied.listFiles()) {
				cpRec(f, Paths.get(destination.getAbsolutePath(),
						f.getName()).toFile());
			}
		}
		try {
			Files.copy(copied.toPath(), destination.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
