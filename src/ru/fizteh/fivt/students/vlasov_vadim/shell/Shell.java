package ru.fizteh.fivt.students.vlasov_vadim.shell;

import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

class MyException extends Exception {
	private static final long serialVersionUID = 1L;

	public MyException(String message) {
		super(message);
	}
}
class ExitException extends Exception {
	private static final long serialVersionUID = 1L;
}

public class Shell {

	private static boolean interactiveMode;
	private static String currentDir;

	public static void main(final String[] args) {
		try {
			if (args.length == 0) {
				interactiveMode = true;
			}
			currentDir = System.getProperty("user.dir");
			if (interactiveMode) {
				beginInteractiveMode();
			} else {
				beginCmdMode(args);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}
	
	private static void beginCmdMode(final String[] args) {
		StringBuilder commandline = new StringBuilder();
		for (String arg: args) {
			commandline.append(arg);
			commandline.append(' ');
		}
		String[] commands = (commandline.toString()).trim().split(";");
		execCommands(commands);
	}

	private static void beginInteractiveMode() {
		try (Scanner s = new Scanner(System.in)) {
			while (true) {
				System.out.print("$ ");
				String line = s.nextLine();
				String[] commands = (line.toString()).trim().split(";");
				execCommands(commands);
			}
		}
	}

	public static void execCommands(final String[] commands) {
		try {
			for (String command : commands) {
				execute(command);
			}
		} catch (ExitException e) {
			System.exit(0);
		}
	}
	
	private static void execute(final String commandline) 
			throws ExitException {
		String commandlinetrimmed = commandline.trim();
		final String[] commands = commandlinetrimmed.split("\\s+");
		try {
			switch (commands[0]) {
			case "pwd":
				pwd(commands);
				break;
			case "cd":
				cd(commands);
				break;
			case "ls":
				ls(commands);
				break;
			case "cat":
				cat(commands);
				break;
			case "mkdir":
				mkdir(commands);
				break;
			case "rm":
				rm(commands);
				break;
			case "cp":
				cp(commands);
				break;
			case "mv":
				mv(commands);
				break;
			case "exit":
				exit(commands);
				break;
			case "":
				break;
			default:
				throw new MyException(
						commands[0]
								+ ": invalid command");
			}
		} catch (MyException e) {
			System.err.println(e.getMessage());
			if (!interactiveMode) {
				System.exit(-1);
			}
		}
	}

	private static void pwd(final String[] args)
			throws MyException {
		if (args.length != 1) {
			throw new MyException(
					"pwd: wrong number of arguments");
		}
		System.out.println(currentDir);
	}

	private static void ls(final String[] args)
			throws MyException {
		if (args.length != 1) {
			throw new MyException(
					"ls: wrong number of arguments");
		}
		File directoryHolder = new File(currentDir);
		String[] fileNamesList = directoryHolder.list();
		for (String s : fileNamesList) {
			System.out.println(s);
		}
	}

	private static void cd(final String[] args)
			throws MyException {
		if (args.length != 2) {
			throw new MyException(
					"cd: wrong number of arguments");
		}
		File currentFile;
		if (args[1].charAt(0) == '/') {
			currentFile = new File(args[1]);
		} else {
			currentFile = new File(currentDir
					+ File.separator + args[1]);
		}
		try {
			if (currentFile.exists() && currentFile.isDirectory()) {
				System.setProperty("user.dir",
						currentFile.getCanonicalPath());
				currentDir = System.getProperty("user.dir");
			} else {
				throw new IOException();
			}
		} catch (IOException e) {
			throw new MyException("cd: " + args[1]
							+ ": no such file or directory");
		}
	}

	private static void cat(final String[] args)
			throws MyException {
		if (args.length != 2) {
			throw new MyException(
					"cat: wrong number of arguments");
		}
		File current = new File(currentDir
				+ File.separator + args[1]);
		if (current.isDirectory() && current.exists()) {
			throw new MyException(
					"cat: " + args[1] + ": is a directory");
		}
		try {
			try (BufferedReader reader = new BufferedReader(
					new FileReader(current))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			throw new MyException(
					"cat: " + args[1]
					+ ": no such file or directory");
		}
	}

	private static void mkdir(final String[] args)
			throws MyException {
		if (args.length != 2) {
			throw new MyException(
					"mkdir: wrong number of arguments");
		}
		File directoryHolder = new File(currentDir
				+ File.separator + args[1]);
		if (directoryHolder.exists()) {
			throw new MyException(
					"mkdir: " + args[1]
					+ ": directory already exists");
		} else if (!directoryHolder.mkdir()) {
			throw new MyException(
				"mkdir: cannot create directory");
		}
	}

	private static void delete(final File file)
			throws MyException {
		if (file.isDirectory()) {
			File[] names = file.listFiles();
			if (names == null) {
				throw new MyException(
						"rm: error");
			}
			for (File f: names) {
				delete(f);
			}
		}
		if (!file.delete()) {
			throw new MyException(
					"rm: error");
		}
	}

	private static void rm(final String[] args)
			throws MyException {
		if (args.length != 2 && args.length != 3) {
			throw new MyException(
					"rm: wrong number of arguments");
		}
		boolean recursive = false;
		if (args.length == 3) {
			if (!args[1].equals("-r")) {
				throw new MyException(
						"rm: " + args[1]
						+ ": invalid argument");
			}
			recursive = true;
		}
		File currentFile = new File(
				currentDir + File.separator
				+ args[args.length - 1]);
		if (!currentFile.exists()) {
			throw new MyException(
					"rm: " + args[args.length - 1] 
					+ ": no such file or directory");
		}
		if (currentFile.isDirectory() && !recursive) {
			throw new MyException(
					"rm: " + args[args.length - 1]
					+ ": is a directory");
		}
		delete(currentFile);
	}

	private static void mv(final String[] args)
			throws MyException {
		if (args.length != 3) {
			throw new MyException(
					"mv: wrong number of arguments");
		}
		File from = new File(currentDir
				+ File.separator + args[args.length - 2]);
		File to = new File(currentDir
				+ File.separator + args[args.length - 1]);
		if (!from.exists()) {
			throw new MyException(
					"mv: " + from.getName()
					+ ": no such file or directory");
		}
		if (from.equals(to)) {
			return;
		}
		try {
			copy(from, to);
		} catch (MyException e) {
			String msg = e.getMessage();
			msg = msg.replaceFirst("cp", "mv");
			throw new MyException(msg);
		}
		try {
			delete(from);
		} catch (MyException e) {
			String msg = e.getMessage();
			msg = msg.replaceFirst("rm", "mv");
			throw new MyException(msg);
		}
	}

	private static void copy(final File from, final File to)
			throws MyException {
		File destination = null;
		try {
			if (to.exists()) {
				if (!to.isDirectory()) {
					throw new MyException(
							"cp: " + to.getName()
							+ ": file already exists");
				} else {
					destination = new File(to.getCanonicalPath()
							+ File.separator + from.getName());
				}
			} else {
				destination = to;
			}
			Files.copy(from.toPath(), destination.toPath(),
					StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			throw new MyException(
					"cp: error");
		}
		if (from.isDirectory()) {
			File[] fileNamesList = from.listFiles();
			for (File f: fileNamesList) {
				copy(f, destination);
			}
		}
	}

	private static void cp(final String[] args)
			throws MyException {
		if (args.length != 3 && args.length != 4) {
			throw new MyException(
					"cp: wrong number of arguments");
		}
		boolean recursive = false;
		if (args.length == 4) {
			if (!args[1].equals("-r")) {
				throw new MyException(
						"rm: " + args[1] + ": invalid argument");
			}
			recursive = true;
		}
		File from = new File(currentDir
				+ File.separator + args[args.length - 2]);
		File to = new File(currentDir
				+ File.separator + args[args.length - 1]);
		if (!from.exists()) {
			throw new MyException(
					"cp: " + from.getName() 
					+ ": no such file or directory");
		}
		if (from.isDirectory() && !recursive) {
			throw new MyException(
					"cp: " + from.getName() + ": is a directory");
		}
		copy(from, to);
	}

	private static void exit(final String[] args)
			throws MyException, ExitException {
		if (args.length != 1) {
			throw new MyException("exit: wrong number of arguments");
		}
		throw new ExitException();
	}
}

