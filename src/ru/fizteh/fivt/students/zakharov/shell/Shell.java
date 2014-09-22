package ru.fizteh.fivt.students.zakharov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Shell {
	// String wd = System.getProperty("user.dir");
	Path wd = null;
	boolean interactive;

	public Shell(boolean interactive) {
		try {
			wd = Paths.get("").toRealPath();
		} catch (IOException e) {
			error("Shell: io error on init");
			System.exit(1);
		}
		this.interactive = interactive;
	}

	/**
	 * Interprets a string array as a command to the shell.
	 * 
	 * @param cmd
	 *            First element must be a valid command, the rest is passed as
	 *            arguments to it.
	 */
	public void interpret(String[] cmd) {
		switch (cmd[0]) {
		case "pwd":
			cmdPwd(cmd);
			break;
		case "cd":
			cmdCd(cmd);
			break;
		case "mkdir":
			cmdMkdir(cmd);
			break;
		case "rm":
			cmdRm(cmd);
			break;
		case "cp":
			cmdCp(cmd);
			break;
		case "mv":
			cmdMv(cmd);
			break;
		case "ls":
			cmdLs(cmd);
			break;
		case "cat":
			cmdCat(cmd);
			break;
		case "exit":
			cmdExit(cmd);
			break;
		default:
			error("Command not found");
		}
	}

	void output(String s) {
		System.out.println(s);
	}

	/**
	 * Output an erorr message to stderr and abort.
	 * 
	 * @param err
	 *            A message to display
	 */
	void error(String err) {
		System.err.println(err);
		if (!interactive) {
			System.exit(1);
		}
	}

	void cmdPwd(String[] cmd) {
		output(wd.toString());
	}

	void cmdCd(String[] cmd) {
		String pathString = (cmd.length < 2) ? System.getProperty("user.home") : cmd[1];
		try {
			wd = wd.resolve(pathString).toRealPath();
		} catch (IOException e) {
			error("cd: '" + pathString + "': No such file or directory");
			return;
		}
	}

	void cmdMkdir(String[] cmd) {
		if (cmd.length < 2) {
			error("mkdir: missing operand");
			return;
		}
		Path path = wd.resolve(cmd[1]);
		try {
			Files.createDirectory(path);
		} catch (FileAlreadyExistsException e) {
			error("mkdir: cannot create directory '" + cmd[1] + "': File exists");
			return;
		} catch (IOException e) {
			error("mkdir: cannot create directory '" + cmd[1] + "': No such file or directory");
			return;
		}
	}

	void cmdRm(String[] cmd) {
		for (int i = 1; i < cmd.length; ++i) {
			Path path = Paths.get(cmd[i]).toAbsolutePath();
			try {
				Files.delete(path);
			} catch (NoSuchFileException x) {
				error("rm: '" + path + "': No such file or directory");
				return;
			} catch (DirectoryNotEmptyException x) {
				error("rm: cannot remove '" + path + "': Is a directory");
				return;
			} catch (IOException x) {
				error("rm: cannot remove '" + path + "': Permission denied");
				return;
			}
		}
	}

	void cmdCp(String[] cmd) {
		if (cmd.length < 3) {
			error("cp: missing file operand");
			return;
		}
		boolean recursive = cmd[1].equals("-r");
		if (recursive && cmd.length < 4) {
			error("cp: missing file operand");
			return;
		}

		Path src = null;
		try {
			src = Paths.get(cmd[1]).toRealPath();
		} catch (IOException e) {
			error("cp: '" + cmd[1] + "': No such file or directory");
			return;
		}

		Path target = Paths.get(cmd[2]);

		boolean isSourceDir = Files.isDirectory(src);
		if (isSourceDir && !recursive) {
			error("cp: omitting directory '" + cmd[1] + "'");
			return;
		}

		boolean isTargetDir = Files.isDirectory(target);
		Path dest = (!isTargetDir) ? target : target.resolve(src.getFileName());

		output(src.toString());
		output(target.toString());
		output(dest.toString());

		try {
			Files.copy(src, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void cmdMv(String[] cmd) {
		output("mv");
	}

	void cmdLs(String[] cmd) {
		output("ls");
	}

	void cmdCat(String[] cmd) {
		if (cmd.length < 1) {
			error("cat: missing file operand");
			return;
		}
		Path path = wd.resolve(cmd[1]);
		try (InputStream in = Files.newInputStream(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			error("cat: " + cmd[1] + ": No such file or directory");
			return;
		}
	}

	void cmdExit(String[] cmd) {
		System.exit(0);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			String argsc = new String();
			for (String s : args) {
				argsc += s + " ";
			}
			String[] cmds = argsc.split(";");
			Shell shell = new Shell(false);

			for (String s : cmds) {
				shell.interpret(s.trim().split("\\s"));
			}
		} else {
			Scanner scanner = new Scanner(System.in);
			scanner.useDelimiter("\\s*[;\\n]\\s*");
			Shell shell = new Shell(true);

			System.out.print("$ ");
			while (scanner.hasNext()) {
				shell.interpret(scanner.next().split("\\s+"));
				System.out.print("$ ");
			}

			scanner.close();
		}
	}
}
