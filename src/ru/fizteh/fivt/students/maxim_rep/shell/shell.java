package ru.fizteh.fivt.students.maxim_rep.shell;

import java.io.*;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.*;

public class shell {

	public static String CurrentPath;

	public static void main(String[] args) throws IOException {
		CurrentPath = System.getProperty("user.home");

		if (args.length == 0) {
			shell.interactiveMode();
		} else {
			shell.commandMode(args);
		}
	}

	public static int commandMode(String[] args) throws IOException {
		String commandline = parser.makeStringCommand(args);

		CurrentPath = System.getProperty("user.home");
		String[] commandsString = parser.DevideByChar(commandline, ";");
		for (String commandsString1 : commandsString) {
			String ConvertedString = CurrentPath;
			if (CurrentPath.startsWith(System.getProperty("user.home"))) {
				ConvertedString = "~"
						+ CurrentPath.substring(System.getProperty("user.home")
								.length());
			}
			System.out.println(System.getProperty("user.name") + "@"
					+ System.getProperty("os.name") + " " + ConvertedString
					+ " $ " + commandsString1);
			shellCommand command = parser.GetCommandFromString(commandsString1);
			if (!command.execute()) {
				System.exit(-1);
				return -1;
			}
		}
		System.exit(0);
		return 0;
	}

	public static void interactiveMode() throws IOException {
		CurrentPath = System.getProperty("user.home");
		for (int i = 0; i == 0;) {
			String ConvertedString = CurrentPath;
			if (CurrentPath.startsWith(System.getProperty("user.home"))) {
				ConvertedString = "~"
						+ CurrentPath.substring(System.getProperty("user.home")
								.length());
			}
			System.out.print(System.getProperty("user.name") + "@"
					+ System.getProperty("os.name") + " " + ConvertedString
					+ " $ ");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String line = in.readLine();

			String[] commandsString = parser.DevideByChar(line, ";");
			for (String commandsString1 : commandsString) {
				shellCommand command = parser
						.GetCommandFromString(commandsString1);
				if (command.getClass() == exit.class)
					System.exit(0);
				command.execute();
			}

		}
	}
}
