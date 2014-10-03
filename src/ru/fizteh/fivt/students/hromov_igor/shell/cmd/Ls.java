package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

import java.io.File;

public class Ls {
	public static void run(String[] args) throws Exception{
		if (args.length > 1) {
			throw new Exception("ls : wrong number of arguments");
		}
		try {
			String[] dirList = new File(System.getProperty("user.dir")).list();
			for (String name : dirList) {
				System.out.println(name);
			}
		} catch (SecurityException e) {
			throw new Exception("ls: access denied");
		}
	}
}
