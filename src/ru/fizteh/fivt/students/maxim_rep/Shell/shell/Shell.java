package shell;

import java.io.*;

import shell.commands.*;

public class Shell {

	public static String CurrentPath;

	public static void main(String[] args) throws IOException {
		CurrentPath = System.getProperty("user.home");

		if (args.length == 0) {
			Shell.interactiveMode();
		} else {
			Shell.commandMode(args);
		}
	}

	public static void commandMode(String[] args) throws IOException {
		String commandline = "";
		for (int i = 0; i < args.length; i++)
			commandline = commandline + args[i];

		CurrentPath = System.getProperty("user.home");
		String ConvertedString = CurrentPath;
		if (CurrentPath.startsWith(System.getProperty("user.home"))) {
			ConvertedString = "~"
					+ CurrentPath.substring(System.getProperty("user.home")
							.length());
		}
		System.out
				.print(System.getProperty("user.name") + "@"
						+ System.getProperty("os.name") + " " + ConvertedString
						+ " $ ");
		String[] commandsString = shell.Parser.DevideByChar(commandline, ";");
		for (String commandsString1 : commandsString) {
			ShellCommand command = shell.Parser
					.GetCommandFromString(commandsString1);
			boolean exit = !command.execute();
			if (exit) {
				return;
			}
		}
		return;
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
			String[] commandsString = shell.Parser.DevideByChar(line, ";");
			for (String commandsString1 : commandsString) {
				ShellCommand command = shell.Parser
						.GetCommandFromString(commandsString1);
				boolean exit = !command.execute();
				if (exit) {
					return;
				}
			}

		}
	}
}
