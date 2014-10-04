package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

public class Pwd {
	public static void run(String[] args) throws Exception {
		if (args.length > 1) {
			throw new Exception("pwd : too much arguments");
		}
		try {
			System.out.println(System.getProperty("user.dir"));
		} catch (SecurityException e) {
			throw new Exception("pwd : access denied");
		}
	}
}
