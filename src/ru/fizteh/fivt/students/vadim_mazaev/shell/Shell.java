package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.util.ArrayList;
//import java.util.Scanner;

public abstract class Shell {
	public static void commandMode(final String[] args) {
		ArrayList<String> cmdWithArgs = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			int separatorIndex = args[i].indexOf(";");
			if (separatorIndex != -1) {
				String part = args[i].substring(0, separatorIndex);
				if (!part.isEmpty()) {
					cmdWithArgs.add(part.trim()); //trim ?
					builder.append(part.trim());
				}
				System.out.println("$ " + builder.toString());
				ShellParser.parse(cmdWithArgs.
						toArray(new String[cmdWithArgs.size()]));
				cmdWithArgs.clear();
				builder.setLength(0);
			} else {
				cmdWithArgs.add(args[i]);
				builder.append(args[i]);
				builder.append(" ");
			}
		}
		System.out.println("$ " + builder.toString().trim());
		ShellParser.parse(cmdWithArgs.
				toArray(new String[cmdWithArgs.size()]));
	}
	
	public static void interactiveMode() {
		//nothing
	}
}
