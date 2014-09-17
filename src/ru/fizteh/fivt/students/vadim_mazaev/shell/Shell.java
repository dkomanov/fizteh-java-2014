package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.util.ArrayList;
import java.util.Scanner;

public final class Shell {
	private Shell() {
		//not called
	}
	
	public static void commandMode(final String[] args) {
		ArrayList<String> cmdWithArgs = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			int separatorIndex = args[i].indexOf(";");
			if (separatorIndex != -1) {
				String part = args[i].substring(0, separatorIndex);
				if (!part.isEmpty()) {
					cmdWithArgs.add(part);
					builder.append(part);
				}
				System.out.println("$ " + builder.toString().trim());
				ShellParser.parse(cmdWithArgs.
						toArray(new String[cmdWithArgs.size()]), true);
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
				toArray(new String[cmdWithArgs.size()]), true);
	}
	
	public static void interactiveMode() {
		ArrayList<String> cmdWithArgs = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		try (Scanner in = new Scanner(System.in)) {
			while (true) {
				System.out.print("$ ");
				String line = in.nextLine();
				String[] commands = line.split(";");
				for (String str : commands) {
					char[] currentCmd = str.trim().toCharArray();
					boolean quoteOpened = false;
					for (int i = 0; i < currentCmd.length; i++) {
						if (currentCmd[i] == ' ' && !quoteOpened) {
							cmdWithArgs.add(builder.toString());
							builder.setLength(0);
						} else if (currentCmd[i] == '\"') {
							quoteOpened = !quoteOpened;
						} else {
							builder.append(currentCmd[i]);
						}
					}
					if (quoteOpened) {
						System.out
						.println("Error parsing statement:"
								+ " odd number of quotes");
					}
					cmdWithArgs.add(builder.toString());
					builder.setLength(0);
					ShellParser.parse(cmdWithArgs.
							toArray(new String[cmdWithArgs.size()]), false);
					cmdWithArgs.clear();
				}
			}
		}
	}
}
