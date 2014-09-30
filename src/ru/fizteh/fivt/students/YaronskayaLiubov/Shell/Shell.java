package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.util.*;
import java.io.*;

public class Shell {
	public static File curDir;

	private static HashMap<String, Command> ShellCommands;

	public static void exec(String[] args) throws IOException {
		curDir = new File(System.getProperty("user.dir"));
		curDir.mkdirs();
		ShellCommands = new HashMap<String, Command>();
		ShellCommands.put("cat", new CatCommand());
		ShellCommands.put("cd", new CdCommand());
		ShellCommands.put("cp", new CpCommand());
		ShellCommands.put("exit", new ExitCommand());
		ShellCommands.put("ls", new LsCommand());
		ShellCommands.put("mkdir", new MkdirCommand());
		ShellCommands.put("mv", new MvCommand());
		ShellCommands.put("pwd", new PwdCommand());
		ShellCommands.put("rm", new RmCommand());
		if (args.length == 0) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				System.out.print(curDir.getAbsolutePath() + "$ ");
				String line;
				line = br.readLine();
				StringTokenizer strTokenizer = new StringTokenizer(line, " ");
				Vector<String> argv = new Vector<String>();
				while (true) {
					try {
						String tmp = strTokenizer.nextToken();
						argv.add(tmp);
					} catch (NoSuchElementException e) {
						break;
					}
				}
				if (argv.size() == 0) {
					System.out.println("Command not found");
					continue;
				}
				String[] arrayArgv = new String[argv.size()];
				for (int i = 0; i < argv.size(); ++i)
					arrayArgv[i] = argv.elementAt(i);
				Vector<String[]> newArgv = ParseArguements.execute(arrayArgv);

				for (int i = 0; i < newArgv.size(); ++i) {
					String curCommand = newArgv.elementAt(i)[0];
					try {
						ShellCommands.get(curCommand).execute(
								newArgv.elementAt(i));
					} catch (NullPointerException e) {
						System.out.println(newArgv.elementAt(i)[0]
								+ ": command not found");
					}
				}
			}
		} else {
			Vector<String[]> newArgv = ParseArguements.execute(args);

			for (int i = 0; i < newArgv.size(); ++i) {
				String curCommand = newArgv.elementAt(i)[0];
				try {
					ShellCommands.get(curCommand).execute(newArgv.elementAt(i));
				} catch (NullPointerException e) {
					System.out.println(newArgv.elementAt(i)[0]
							+ ": command not found");
				}
			}
		}
	}

	public static void fileCopy(InputStream is, OutputStream os)
			throws IOException {
		int nLength;
		byte[] buf = new byte[8000];
		while (true) {
			nLength = is.read(buf);
			if (nLength < 0)
				break;
			os.write(buf, 0, nLength);
		}
	}

	public static void dirCopy(File myFile, File DirDestination)
			throws IOException {
		if (myFile.isDirectory()) {
			File newDir = new File(DirDestination, myFile.getName());
			newDir.mkdirs();
			File[] content = myFile.listFiles();
			for (int i = 0; i < content.length; ++i) {
				dirCopy(content[i], newDir);
			}
		} else {
			FileInputStream fis = new FileInputStream(myFile);
			File newFile = new File(DirDestination, myFile.getName());
			if (!newFile.exists()) {
				newFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(newFile);
			fileCopy(fis, fos);
			fis.close();
		}

	}

	public static void fileDelete(File myDir) {
		if (myDir.isDirectory()) {
			File[] content = myDir.listFiles();
			for (int i = 0; i < content.length; ++i)
				fileDelete(content[i]);
		}
		myDir.delete();
	}

	public static void main(String[] argv) {
		try {
			Shell.exec(argv);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
