package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;
//package Shell;

public class PwdCommand extends Command {
	PwdCommand() {
		name = "pwd";
		maxNumberOfArguements = 1;
	}

	boolean execute(String[] args) {
		if (args.length > maxNumberOfArguements) {
			return false;
		}
		try {
			System.out.println(Shell.curDir.getCanonicalPath());
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		return true;
	}
}
