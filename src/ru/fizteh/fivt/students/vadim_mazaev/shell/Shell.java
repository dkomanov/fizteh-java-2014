package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.util.ArrayList;
//import java.util.Scanner;

public abstract class Shell {
	public static void main(final String[] args) {
		if (args.length > 0) {
			//package
			ArrayList<String> buffer = new ArrayList<String>();
			for (int i = 0; i < args.length; i++) {
				int separatorIndex = args[i].indexOf(";");
				if (separatorIndex != -1) {
					String part = args[i].substring(0, separatorIndex);
					if (!part.isEmpty()) {
						buffer.add(part.trim());
					}
					parserStarter(buffer);
				} else {
					buffer.add(args[i]);
				}
			}
			parserStarter(buffer);
			System.exit(0);
		} else {
			//interactive
		}
	}
	
	private static void parserStarter(final ArrayList<String> buffer) {
		StringBuilder builder = new StringBuilder();
		String[] cmdWithArgs = new String[buffer.size()];
		int j = 0;
		for (String str : buffer) {
			cmdWithArgs[j++] = str;
			if (str.indexOf(" ") != -1) {
				builder.append("\"");
			}
			builder.append(str);
			if (str.indexOf(" ") != -1) {
				builder.append("\"");
			}
			builder.append(" ");
		}
		buffer.clear();
		builder.deleteCharAt(builder.length() - 1);
		System.out.println("$ " + builder.toString());
		if (!ShellParser.parse(cmdWithArgs)) {
			System.exit(1);
		}
	}
}
