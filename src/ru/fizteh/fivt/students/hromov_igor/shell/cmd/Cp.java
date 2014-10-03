package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Cp {
	public static void run(String[] args) throws Exception{
		int afterKeyIndex = 1;
		if (args.length > 2 && args[1].equals("-r")){
			afterKeyIndex = 2;
		}
		if (args.length <= 2
				|| (args.length <= 3 && args[1].equals("-r"))) {
			throw new Exception("cp : wrong number of arguments");
		} else { if 
			(args.length > 4 || (args.length > 3 && args[1].equals("-r"))){
			throw new Exception("cp : wrong number of arguments");
		}
		}
		try{
			File file = Paths.get(args[afterKeyIndex]).normalize().toFile();
			if (!file.isAbsolute()) {
					file = Paths.get(System.getProperty("user.dir"),args[afterKeyIndex]).normalize().toFile();
			}
			if (args[1].isEmpty() || !file.exists()) {
				throw new Exception("cp : no such file or directory");
			}
			File destFile = Paths.get(args[afterKeyIndex]).normalize().toFile();
			if (!destFile.isAbsolute()) {
				destFile = Paths.get(System.getProperty("user.dir"),args[afterKeyIndex + 1]).normalize().toFile();
			}
			if (args[afterKeyIndex + 1].isEmpty()) {
				throw new Exception("cp : no such file or directory");
			}
			if (file.isFile()) {
				if (!destFile.getParentFile().exists()) {
					throw new Exception("cp : cannot create not a directory");
				}
				if (destFile.isDirectory()) {
					destFile = Paths.get(destFile.getPath(),file.getName()).toFile();
				}
				if (destFile.toPath().toString().equals(file.toPath().toString())) {
					throw new Exception("cp : cannot copy file into itself");
				}	
				Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} else {
				if (afterKeyIndex == 1) {
					throw new Exception("cp : cant copy a directory");
				}
				String compare = destFile.toPath()
						.relativize(file.toPath()).toString();
				if (compare.equals("") || compare.matches("[\\/\\.]+")) {
					throw new Exception("cp : cannot copy directory into itself");
				}
				if (!destFile.getParentFile().exists()) {
					throw new Exception("cp : No such file or directory");
				}
				if (destFile.exists()) {
					destFile = Paths.get(destFile.getPath(),
							file.getName()).toFile();
				}
				if (!cpRec(file, destFile)) {
					throw new Exception("cp : cannot copy file");
				}	
			}
		} catch(IOException e) {
			throw new Exception("rm : cannot read or write files");
		} catch (InvalidPathException e) {
			throw new Exception("rm : wrong name of file");
		} catch (SecurityException e) {
			throw new Exception("rm : access denied");
		}
	}
	
	public static boolean cpRec(File copied,  File destination) {
		if (copied.isDirectory()) {
			destination.mkdir();
			for (File f : copied.listFiles()) {
				if (!cpRec(f, Paths.get(destination.getAbsolutePath(), f.getName()).toFile())) {
					return false;
				}
			}
		} else {
			try {
				Files.copy(copied.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
}
