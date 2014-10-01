package ru.fizteh.fivt.students.irina_karatsapova.shell; 

import ru.fizteh.fivt.students.irina_karatsapova.shell.commands.*;
import ru.fizteh.fivt.students.irina_karatsapova.shell.Shell;

public class Main {	
	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.addCommand(new CatCommand());
		shell.addCommand(new CdCommand());
		shell.addCommand(new CpCommand());
		shell.addCommand(new ExitCommand());
		shell.addCommand(new LsCommand());
		shell.addCommand(new MkdirCommand());
		shell.addCommand(new MvCommand());
		shell.addCommand(new PwdCommand());
		shell.addCommand(new RmCommand());
		if (args.length == 0) {
			shell.interactiveMode();
		} else {
			String wholeArgument = Utils.concatStrings(args, " ");
			try {
				shell.batchMode(wholeArgument);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
	}
}

