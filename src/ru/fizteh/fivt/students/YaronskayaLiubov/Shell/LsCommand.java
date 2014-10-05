package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;
//package Shell;

public class LsCommand extends Command {
	boolean execute(String[] args) {
		String[] content = Shell.curDir.list();
		if (content == null) {
			return false;
		}
		if (args.length > maxNumberOfArguements) {
			return false;
		}
		for (int i = 0; i < content.length; ++i) {
			System.out.println(content[i]);
		}
		return true;
	}

	LsCommand() {
		name = "ls";
		maxNumberOfArguements = 1;
	}
}
