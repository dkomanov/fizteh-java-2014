package ru.fizteh.fivt.students.maxim_rep.shell;

import java.io.File;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.*;

public class Parser {

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

	public static ShellCommand GetCommandFromString(String str) {
		String[] comArgs = ru.fizteh.fivt.students.maxim_rep.shell.Parser.DevideByChar(str, " ");
		String comName = comArgs[0];

		try {
			switch (comName) {
			case "exit":
				return new Exit();
			case "cd":
				return new Cd(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[1]);
			case "cat":
				return new Cat(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[1]);
			case "mv":
				return new Mv(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[1], comArgs[2]);
			case "cp":
				if (comArgs[1].equals("-r"))
					return new Cp(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[2],
							comArgs[3], true);
				else
					return new Cp(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[1],
							comArgs[2], false);
			case "rm":
				if (comArgs[1].equals("-r"))
					return new Rm(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[2], true);
				else
					return new Rm(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[1], false);
			case "mkdir":
				return new Mkdir(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath, comArgs[1]);
			case "":
				return new EmptyCommand();
			case "ls":
				return new Ls(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath);
			case "pwd":
				return new Pwd(ru.fizteh.fivt.students.maxim_rep.shell.Shell.CurrentPath);
			default:
				return new UnknownCommand(str);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Wrong command syntax");
			return new EmptyCommand();

		}
	}
}
