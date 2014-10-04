package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

public class Mv {
	public static void run(String[] args) throws Exception {
		if (args.length != 3) {
			throw new Exception("rm : wrong number of arguments");
		}

		String[] newArgs = new String[4];

		newArgs[0] = args[0];
		newArgs[1] = "-r";
		newArgs[2] = args[1];
		newArgs[3] = args[2];

		Cp.run(newArgs);

		newArgs = new String[3];
		newArgs[0] = args[0];
		newArgs[1] = "-r";
		newArgs[2] = args[1];
		Rm.run(newArgs);
	}
}
