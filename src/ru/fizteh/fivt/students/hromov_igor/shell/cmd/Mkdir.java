package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Mkdir {
	public static void run(String[] args) throws Exception {
		if (args.length > 2) {
			throw new Exception("mkdir : wrong number of arguments");
		}
		try {
			if (args[1].isEmpty()) {
				throw new Exception("mkdir : cannot create folder");
			}
			File dir = Paths.get(System.getProperty("user.dir"), args[1])
					.toFile();
			if (!dir.mkdir()) {
				throw new Exception("mkdir : folder already exists");
			}
		} catch (SecurityException e) {
			throw new Exception("mkdir : cannot create folder : access deneid");
		} catch (InvalidPathException e) {
			throw new Exception("mkdir : wrong name of folder");
		}
	}
}
