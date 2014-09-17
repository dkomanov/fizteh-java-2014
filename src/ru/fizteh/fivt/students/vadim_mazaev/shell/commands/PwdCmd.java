package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

public final class PwdCmd {
	private PwdCmd() {
		//not called
	}
	
	public static void run(final String[] cmdWithArgs) throws Exception {
		if (cmdWithArgs.length > 1) {
			throw new IllegalArgumentException(getName()
				+ ": two much arguments");
		}
		try {
			System.out.println(System.getProperty("user.dir"));
		} catch (SecurityException e) {
			throw new SecurityException(getName()
				+ ": cannot read current working directory: access denied");
		}
	}
	
	public static String getName() {
		return "pwd";
	}
}
