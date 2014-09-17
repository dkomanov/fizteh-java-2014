package ru.fizteh.fivt.students.vadim_mazaev.shell;

import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.LsCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.CatCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.PwdCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.CdCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.CpCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.MkdirCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.RmCmd;

public abstract class ShellParser {
	public static void parse(final String[] cmdWithArgs) {
		try {
			if (cmdWithArgs.length > 0) {
				switch (cmdWithArgs[0]) {
				case "exit":
					System.exit(0);
				case "pwd":
					PwdCmd.run(cmdWithArgs);
					break;
				case "ls":
					LsCmd.run(cmdWithArgs);
					break;
				case "cd":
					CdCmd.run(cmdWithArgs);
					break;
				case "mkdir":
					MkdirCmd.run(cmdWithArgs);
					break;
				case "cat":
					CatCmd.run(cmdWithArgs);
					break;
				case "rm":
					RmCmd.run(cmdWithArgs);
					break;
				case "cp":
					CpCmd.run(cmdWithArgs);
					break;
				default:
					System.out.println(cmdWithArgs[0] + ": no such command");
					System.exit(1);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
