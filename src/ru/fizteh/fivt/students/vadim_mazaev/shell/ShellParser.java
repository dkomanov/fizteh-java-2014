package ru.fizteh.fivt.students.vadim_mazaev.shell;

import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.LsCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.CatCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.MvCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.PwdCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.CdCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.CpCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.MkdirCmd;
import ru.fizteh.fivt.students.vadim_mazaev.shell.commands.RmCmd;

public final class ShellParser {
	private ShellParser() {
		//not called
	}
	
	public static void parse(final String[] cmdWithArgs,
					final boolean isCmdMode) {
		try {
			if (cmdWithArgs.length > 0 && !cmdWithArgs[0].isEmpty()) {
				switch (cmdWithArgs[0]) {
				case "exit":
					System.exit(0);
                    break;
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
				case "mv":
					MvCmd.run(cmdWithArgs);
					break;
				default:
					System.err.println(cmdWithArgs[0] + ": no such command");
					if (isCmdMode) {
						System.exit(1);
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			if (isCmdMode) {
				System.exit(1);
			}
		}
	}
}
