package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.util.Scanner;

public abstract class Shell {
	public static void main(final String[] args) {
		if (args.length > 0) {
			//package
			StringBuilder buff = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				buff.append(args[i]);
				buff.append(" ");
			}
			String[] cmds = buff.toString().split(";");
			for (int j = 0; j < cmds.length; j++) {
				System.out.println("$ " + cmds[j].trim());
				if (!ShellParser.parse(cmds[j])) {
					System.exit(1);
				}
			}
			System.exit(0);
		} else {
			//interactive
			try {
				System.setProperty("user.dir", System.getProperty("user.home"));
			} catch (SecurityException e) {
				System.out.println("cannot open home directory: access denied");
				System.exit(1);
			}
			Scanner in = new Scanner(System.in);
			try {
				while (true) {
					System.out.print("$ ");
					String[] cmds = in.nextLine().split(";");
					for (int j = 0; j < cmds.length; j++) {
						if (!ShellParser.parse(cmds[j])) {
							break;
						}
					}
				}
			} finally {
				in.close();
			}
		}
	}
}
