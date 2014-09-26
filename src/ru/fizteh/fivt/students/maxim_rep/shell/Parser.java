package ru.fizteh.fivt.students.maxim_rep.shell;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.*;

import java.io.File;

public class Parser {

	public static String makeStringCommand(String[] args) {
		String commandLine = "";
		for (int i = 0; i < args.length; i++) {
			commandLine = commandLine + args[i] + " ";
		}

		return commandLine;
	}

	public static String pathConverter(String Path, String currentPath) {
		File f = new File(currentPath);

		if (Path.equals("/") || Path.equals("\\")) {
			return "/";
		} else if (Path.startsWith("~")) {
			return System.getProperty("user.home") + Path.substring(1);
		} else if (Path.equals("") || Path.equals(" ")) {
			return System.getProperty("user.home");
		} else if (Path.length() >= 2 && Path.substring(0, 2).equals("..")) {
			if (f.getParent() == null) {
				return "/" + Path.substring(2);
			}
			return f.getParent() + Path.substring(2);
		} else if (Path.startsWith(".")) {
			return currentPath + Path.substring(1);
		}
		if (Path.startsWith("\\", 0) || Path.startsWith("/", 0)) {
			Path = "C:" + Path;
		} else if (!(Path.startsWith(":", 1))) {
			Path = currentPath + "\\" + Path;
		}

		if (!(Path.startsWith("/", 0) || Path.startsWith(":", 1))) {
			if (currentPath.equals("/")) {
				Path = currentPath + Path;
			} else {
				Path = currentPath + "/" + Path;
			}
		}

		return Path;
	}

	public static String[] divideByChar(String args, String parseBy) {
		args = args.trim();

		if (parseBy.equals(" ")) {
			char[] temp = args.toCharArray();
			for (int i = 0; i < temp.length - 1; i++) {
				if (temp[i] == '"') {
					for (int j = i + 1; j < temp.length; j++) {
						if (temp[j] != ' ') {
							if (temp[j] == '"') {
								for (int k = i; k <= j; k++) {
									temp[k] = (char) -11;
								}
								temp[i] = (char) -10;
								temp[j] = (char) -10;
								break;
							} else {
								break;
							}
						}
					}
				}

			}
			String[] splited = (new String(temp).split(" "));
			for (int i = 0; i < splited.length; i++) {
				char[] test_temp = { (char) -10 };
				splited[i] = splited[i].replaceAll((new String(test_temp)), "");
				splited[i] = splited[i].replace((char) -11, ' ');
			}
			return splited;
		}

		return args.split(parseBy);
	}

	public static String brakedString(String line) {
		if (line.startsWith("\"") && line.endsWith("\"")) {
			line = line.substring(1, line.length() - 1);
		}
		return line;
	}

	public static ShellCommand getCommandFromString(String str) {
		String[] comArgs = Parser.divideByChar(str, " ");
		String comName = comArgs[0];

		try {
			switch (comName) {
			case "exit":
				return new Exit();
			case "cd":
				return new Cd(Shell.currentPath, comArgs[1]);
			case "cat":
				return new Cat(Shell.currentPath, comArgs[1]);
			case "mv":
				return new Mv(Shell.currentPath, comArgs[1], comArgs[2]);
			case "cp":
				if (comArgs[1].equals("-r")) {
					return new Cp(Shell.currentPath, comArgs[2], comArgs[3],
							true);
				} else {
					return new Cp(Shell.currentPath, comArgs[1], comArgs[2],
							false);
				}
			case "rm":
				if (comArgs[1].equals("-r")) {
					return new Run(Shell.currentPath, comArgs[2], true);
				} else {
					return new Run(Shell.currentPath, comArgs[1], false);
				}
			case "mkdir":
				return new Mkdir(Shell.currentPath, comArgs[1]);
			case "":
				return new EmptyCommand();
			case "ls":
				return new Ls(Shell.currentPath);
			case "pwd":
				return new Pwd(Shell.currentPath);
			default:
				return new UnknownCommand(str);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			if (comName.equals("cd")) {
				return new Cd(Shell.currentPath, "");
			}

			System.out.println(str + ": Wrong command syntax");
			return new EmptyCommand();

		}
	}
}
