package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.util.*;

public class Shell {
	public static void main(String[] args) {
		if (args.length > 0) {
			StringBuilder buff = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				buff.append(args[i]);
				buff.append(" ");
			}
			String[] cmds = buff.toString().split(";");
			for (int j = 0; j < cmds.length; j++) {
				System.out.println("$ " + cmds[j].trim());
				if (!ShellParser.parse(cmds[j]))
					System.exit(1);
			}
			System.exit(0);
		}
		else {
			//interactive
			System.setProperty("user.dir", System.getProperty("user.home"));
			Scanner in = new Scanner(System.in);
			in.close();
		}
	}
}