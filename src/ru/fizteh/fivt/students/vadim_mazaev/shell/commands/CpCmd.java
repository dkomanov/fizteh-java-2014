package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class CpCmd {
	private CpCmd() {
		//not called
	}
	
	public static void run(final String[] cmdWithArgs) throws Exception {
		int afterKeyIndex = 1;
		if (cmdWithArgs[1] == "-r") {
			afterKeyIndex = 2;
		}
		if (cmdWithArgs.length <= 2
				|| (cmdWithArgs.length <= 3 && cmdWithArgs[1] == "-r")) {
			throw new Exception(getName() + ": missing operand");
		} else if (cmdWithArgs.length > 3
				|| (cmdWithArgs.length > 4 && cmdWithArgs[1] != "-r")) {
			throw new Exception(getName()
					+ ": two much arguments");
		}
		try {
			File copiedFile = Paths.get(cmdWithArgs[afterKeyIndex])
									.normalize().toFile();
			if (!copiedFile.isAbsolute()) {
				copiedFile = Paths.get(System.getProperty("user.dir"),
					cmdWithArgs[afterKeyIndex]).normalize().toFile();
			}
			if (!copiedFile.exists()) {
				throw new Exception(getName() + ": "
						+ cmdWithArgs[afterKeyIndex]
						+ ": No such file or directory");
			}
			File destinationFile = Paths.get(cmdWithArgs[afterKeyIndex])
									.normalize().toFile();
			if (!destinationFile.isAbsolute()) {
				destinationFile = Paths.get(System.getProperty("user.dir"),
					cmdWithArgs[afterKeyIndex + 1]).normalize().toFile();
			}
			
			String compare = destinationFile.toPath()
					.relativize(copiedFile.toPath()).toString();
			
			if (compare.equals("") || compare.matches("[\\/\\.]+")) {
				throw new Exception(getName()
						+ ": cannot copy file to its child directory");
			}
			
			if (copiedFile.isFile()) {
				if (destinationFile.isDirectory()) {
					destinationFile = Paths.get(destinationFile.getPath(),
								copiedFile.getName()).toFile();
				}
				Files.copy(copiedFile.toPath(), destinationFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.COPY_ATTRIBUTES);
			} else {
				if (destinationFile.isDirectory()) {
					destinationFile = Paths.get(destinationFile.getPath(),
							copiedFile.getName()).toFile();
				}
				if (!cpRec(copiedFile, destinationFile)) {
					throw new Exception(getName() + ": "
						+ "cannot copy file '"
						+ cmdWithArgs[afterKeyIndex] + "' to '"
						+ cmdWithArgs[afterKeyIndex + 1] + "'");
				}
			}
		} catch (IOException e) {
			throw new Exception(getName()
					+ ": cannot copy file '"
					+ cmdWithArgs[afterKeyIndex] + "' to '"
					+ cmdWithArgs[afterKeyIndex + 1] + "'");
		} catch (InvalidPathException e) {
			throw new Exception(getName()
					+ ": cannot copy file '"
					+ cmdWithArgs[afterKeyIndex] + "' to '"
					+ cmdWithArgs[afterKeyIndex + 1]
					+ "': illegal character in name");
		} catch (SecurityException e) {
			throw new Exception(getName()
					+ ": cannot copy file '"
					+ cmdWithArgs[afterKeyIndex] + "' to '"
					+ cmdWithArgs[afterKeyIndex  + 1]
					+ "': access denied");
		}
	}
	
	private static boolean cpRec(final File copied, final File destination) {
		if (copied.isDirectory()) {
			destination.mkdir();
			for (File f : copied.listFiles()) {
				if (!cpRec(f, Paths.get(destination.getAbsolutePath(),
						f.getName()).toFile())) {
					return false;
				}
			}
		} else {
			try {
				Files.copy(copied.toPath(), destination.toPath(),
						StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	public static String getName() {
		return "cp";
	}
}
