package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.util.*;
import java.io.*;

public class Shell {
	public static File curDir;

	private static HashMap<String, Command> shellCommands;

	public static boolean exec(String[] args) throws IOException {
		curDir = new File(System.getProperty("user.dir"));
		curDir.mkdirs();
		shellCommands = new HashMap<String, Command>();
		shellCommands.put("cat", new CatCommand());
		shellCommands.put("cd", new CdCommand());
		shellCommands.put("cp", new CpCommand());
		shellCommands.put("exit", new ExitCommand());
		shellCommands.put("ls", new LsCommand());
		shellCommands.put("mkdir", new MkdirCommand());
		shellCommands.put("mv", new MvCommand());
		shellCommands.put("pwd", new PwdCommand());
		shellCommands.put("rm", new RmCommand());
		boolean errorOccurred = false;
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
					//System.out.println("Command not found");
					//errorOccurred = true;
					continue;
				}
				String[] arrayArgv = new String[argv.size()];
				for (int i = 0; i < argv.size(); ++i) {
					arrayArgv[i] = argv.elementAt(i);
				}
				Vector<String[]> newArgv = ParseArguements.execute(arrayArgv);

				for (int i = 0; i < newArgv.size(); ++i) {
					String curCommand = newArgv.elementAt(i)[0];
					try {
						if (shellCommands.get(curCommand).execute(
								newArgv.elementAt(i)) == false) {
							errorOccurred = true;
						}
					} catch (NullPointerException e) {
						System.out.println(newArgv.elementAt(i)[0]
								+ ": command not found");
						errorOccurred = true;
					}
				}
			}
		} else {
			Vector<String[]> newArgv = ParseArguements.execute(args);

			for (int i = 0; i < newArgv.size(); ++i) {
				String curCommand = newArgv.elementAt(i)[0];
				try {
					if (shellCommands.get(curCommand).execute(
							newArgv.elementAt(i)) == false) {
						errorOccurred = true;
					}
				} catch (NullPointerException e) {
					System.out.println(newArgv.elementAt(i)[0]
							+ ": command not found");
					errorOccurred = true;
				}
			}
		}
		return !errorOccurred;
	}

	public static void fileCopy(InputStream is, OutputStream os)
			throws IOException {
		int nLength;
		byte[] buf = new byte[8000];
		while (true) {
			nLength = is.read(buf);
			if (nLength < 0) {
				break;
			}
			os.write(buf, 0, nLength);
		}
	}

	public static void dirCopy(File myFile, File dirDestination)
			throws IOException {
		if (myFile.isDirectory()) {
			File newDir = new File(dirDestination, myFile.getName());
			newDir.mkdirs();
			File[] content = myFile.listFiles();
			for (int i = 0; i < content.length; ++i) {
				dirCopy(content[i], newDir);
			}
		} else {
			FileInputStream fis = new FileInputStream(myFile);
			File newFile = new File(dirDestination, myFile.getName());
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
			for (int i = 0; i < content.length; ++i) {
				fileDelete(content[i]);
			}
		}
		myDir.delete();
	}

	public static void main(String[] argv) {
		boolean errorOccurred;
		try {
			errorOccurred = !Shell.exec(argv);
		} catch (Exception e) {
			System.out.println(e.toString());
			errorOccurred = true;
		}
		if (errorOccurred) {
			System.exit(1);
		} else {
			System.exit(0);
		}
	}
}
