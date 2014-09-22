package ru.fizteh.fivt.students.maxim_rep.shell;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.*;

import java.io.File;

public class parser {

	public static String makeStringCommand(String[] args) {
		String commandLine = "";
		for (int i = 0; i < args.length; i++) {
			commandLine = commandLine + args[i] + " ";
		}

		return commandLine;
	}

	public static String PathConverter(String Path, String CurrentPath) {
		File f = new File(CurrentPath);

		if (Path.equals("/"))
			return "/";
		else if (Path.startsWith("~"))
			return System.getProperty("user.home") + Path.substring(1);
		else if (Path.equals("") || Path.equals(" "))
			return System.getProperty("user.home");
		else if (Path.length() >= 2 && Path.substring(0, 2).equals("..")) {
			if (f.getParent() == null) {
				return "/" + Path.substring(2);
			}
			return f.getParent() + Path.substring(2);
		} else if (Path.startsWith("."))
			return CurrentPath + Path.substring(1);

		if (!(Path.startsWith("/", 0))) {
			if (CurrentPath.equals("/")) {
				Path = CurrentPath + Path;
			} else {
				Path = CurrentPath + "/" + Path;
			}
		}
		return Path;
	}

	public static String[] DevideByChar(String args, String ParseBy) {
		args = args.trim();
		return args.split(ParseBy);
	}

	public static String brakedString(String line) {
		if (line.startsWith("\"") && line.endsWith("\""))
			line = line.substring(1, line.length() - 1);
		return line;
	}

	public static shellCommand GetCommandFromString(String str) {
		String[] comArgs = parser.DevideByChar(str, " ");
		String comName = comArgs[0];

		for (int i = 0; i < comArgs.length; i++)
			comArgs[i] = brakedString(comArgs[i]);

		try {
			switch (comName) {
			case "exit":
				return new exit();
			case "cd":
				return new cd(shell.CurrentPath, comArgs[1]);
			case "cat":
				return new cat(shell.CurrentPath, comArgs[1]);
			case "mv":
				return new mv(shell.CurrentPath, comArgs[1], comArgs[2]);
			case "cp":
				if (comArgs[1].equals("-r"))
					return new cp(shell.CurrentPath, comArgs[2], comArgs[3],
							true);
				else
					return new cp(shell.CurrentPath, comArgs[1], comArgs[2],
							false);
			case "rm":
				if (comArgs[1].equals("-r"))
					return new run(shell.CurrentPath, comArgs[2], true);
				else
					return new run(shell.CurrentPath, comArgs[1], false);
			case "mkdir":
				return new mkdir(shell.CurrentPath, comArgs[1]);
			case "":
				return new emptyCommand();
			case "ls":
				return new ls(shell.CurrentPath);
			case "pwd":
				return new pwd(shell.CurrentPath);
			default:
				return new unknownCommand(str);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			if (comName.equals("cd"))
				return new cd(shell.CurrentPath, "");

			System.out.println(str + ": Wrong command syntax");
			return new emptyCommand();

		}
	}
}
