package ru.fizteh.fivt.students.hromov_igor.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Shell {

	public static void interactiveMode() {
		List<String> cmdArgs = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		try (Scanner in = new Scanner(System.in)) {
			while (true) {
				System.out.print("$ ");
				String line = "";
				try {
					line = in.nextLine();
				} catch (NoSuchElementException e) {
					System.exit(0);
				}
				String[] commands = line.split(";");
				for (String str : commands) {
					char[] currentCmd = str.trim().toCharArray();
					boolean quoteOpened = false;
					for (int i = 0; i < currentCmd.length; i++) {
						if (Character.isWhitespace(currentCmd[i])
								&& !quoteOpened) {
							if (builder.length() != 0) {
								cmdArgs.add(builder.toString());
								builder.setLength(0);
							}
						} else if (currentCmd[i] == '\"') {
							quoteOpened = !quoteOpened;
						} else {
							builder.append(currentCmd[i]);
						}
					}
					if (quoteOpened) {
						System.out
								.println("Error parsing statement: odd number of quotes");
					}
					if (builder.length() != 0) {
						cmdArgs.add(builder.toString());
						builder.setLength(0);
					}
					StringParser.parse(
							cmdArgs.toArray(new String[cmdArgs.size()]), false);
					cmdArgs.clear();
				}
			}
		}
	}

	public static void packageMode(String[] args) {
		List<String> cmdArgs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			int separatorIndex = args[i].indexOf(";");
			if (separatorIndex != -1) {
				String part = args[i].substring(0, separatorIndex);
				if (!part.isEmpty()) {
					cmdArgs.add(part);
				}
				StringParser.parse(cmdArgs.toArray(new String[cmdArgs.size()]),
						true);
				part = args[i].substring(separatorIndex + 1);
				if (!part.isEmpty()) {
					cmdArgs.add(part);
				}
			} else {
				cmdArgs.add(args[i]);
			}
		}
		StringParser.parse(cmdArgs.toArray(new String[cmdArgs.size()]), true);
	}
}
