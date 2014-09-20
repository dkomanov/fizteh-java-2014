package ru.fizteh.fivt.students.vadim_mazaev.filemap;

public final class CommandParser {
	private CommandParser() {
		//not called
	}
	
	public static void packageMode(final DbConnector link,
			final String[] args) {
		StringBuilder builder = new StringBuilder();
		for (String current : args) {
			builder.append(current);
		}
		String[] cmds = builder.toString().split(";");
		for (String current : cmds) {
			parse(link, current.trim().split("\\s+"), true);
		}
	}
	public static void interactiveMode(final DbConnector link) {
		//TODO write the mode
	}
	
	public static void parse(final DbConnector link,
			final String[] command, final boolean exitOnError) {
		try {
			if (command.length != 0) {
				switch(command[0]) {
				case "put":
					
					break;
				case "get":
					
					break;
				case "remove":
					
					break;
				case "list":
					
					break;
				case "exit":
					System.exit(0);
					break;
				default:
					throw new
						IllegalArgumentException("No such command declared");
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			if (exitOnError) {
				System.exit(1);
			}
		}
	}
}
