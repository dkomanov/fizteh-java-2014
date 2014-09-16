package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public abstract class ShellParser {
	private static final String REC_PARAM = "-r";
	
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
		case "rm":
			return rm(cmdArgs);
		case "cp":
			return cp(cmdArgs);
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
	
	private static boolean cd(final String cmdArgs) {
		if (cmdArgs.isEmpty()) {
			System.out.println("cd: missing operand");
			return false;
		}
		try {
			File newWorkingDir = Paths.get(cmdArgs).normalize().toFile();
			if (!newWorkingDir.isAbsolute()) {
				newWorkingDir = Paths.get(
						System.getProperty("user.dir"), cmdArgs)
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
	
	private static boolean mkdir(final String cmdArgs) {
		if (cmdArgs.isEmpty()) {
			System.out.println("mkdir: missing operand");
			return false;
		}
		try {	
			File makedDir = Paths.get(
					System.getProperty("user.dir"), cmdArgs).toFile();
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
			File cattedFile = Paths.get(
					System.getProperty("user.dir"), cmdArgs).toFile();
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
	
	private static boolean rm(final String cmdArgs) {
		String cmdArgsWithoutParam = removeParam(cmdArgs, "-r");
		try {
			File removedFile = Paths.get(
					cmdArgsWithoutParam).normalize().toFile();
			if (!removedFile.isAbsolute()) {
				removedFile = Paths.get(System.getProperty("user.dir"),
							cmdArgsWithoutParam).normalize().toFile();
			}
			if (!removedFile.exists()) {
				System.out.println("rm: "
					+ cmdArgsWithoutParam + ": No such file or directory");
				return false;
			}
			if (removedFile.isFile()) {
				if (!removedFile.delete()) {
					System.out.println("rm: " + cmdArgsWithoutParam
							+ ": cannot delete file");
					return false;
				}
				return true;
			} else {
				if (cmdArgsWithoutParam.length() == cmdArgs.length()) {
					System.out.println("rm: " + cmdArgsWithoutParam
							+ ": is a directory");
					return false;
				}
				if (!rmRec(removedFile)) {
					System.out.println("rm: " + cmdArgsWithoutParam
							+ ": cannot delete file");
					return false;
				}
			}
		} catch (InvalidPathException e) {
			System.out.println(
					"rm: cannot remove directory '"
						+ cmdArgsWithoutParam + "': illegal character in name");
		} catch (SecurityException e) {
			System.out.println(
					"rm: cannot remove directory '"
						+ cmdArgsWithoutParam + "': access denied");
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
	
	private static String removeParam(final String cmdArgs, final String key) {
		int recIndex = cmdArgs.indexOf(REC_PARAM + " ");
		if (cmdArgs.isEmpty() || (recIndex == 0
			&& cmdArgs.length() < REC_PARAM.length() + 1)) {
			System.out.println("rm: missing operand");
			return "";
		}
		if (recIndex == 0) {
			return cmdArgs.substring(REC_PARAM.length() + 1);
		}
		return cmdArgs;
	}
	
	private static boolean cp(final String cmdArgs) {
		String cmdArgsWithoutParam = removeParam(cmdArgs, "-r");
		//String cmdArg1 = cmdArgsWithoutParam.indexOf(" ");
		
		return false;
	}
}
