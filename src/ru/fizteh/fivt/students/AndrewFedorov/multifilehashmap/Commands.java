package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

import static ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.Utility.handleError;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Commands {
    static class Create implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }

	    shell.getActiveDatabase().createTable(args[1]);
	}

	@Override
	public String getInfo() {
	    return "creates a new table with the given name";
	}

	@Override
	public String getInvocation() {
	    return "<tablename>";
	}
    }

    static class Drop implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }

	    shell.getActiveDatabase().dropTable(args[1]);
	}

	@Override
	public String getInfo() {
	    return "deletes table with the given name from file system";
	}

	@Override
	public String getInvocation() {
	    return "<tablename>";
	}
    }

    static class Exit implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 1) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }
	    shell.persistDatabase();
	    shell.exit(0);
	}

	@Override
	public String getInfo() {
	    return "saves all data to file and stops interpretation";
	}

	@Override
	public String getInvocation() {
	    return null;
	}
    }

    static class Get implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }
	    String key = args[1];

	    String value = null;
	    try {
		value = shell.getActiveTable().get(key);
	    } catch (DBFileCorruptException | IOException exc) {
		handleError(exc, String.format("Table %s is corrupt", shell
			.getActiveTable().getTableName()), true);
	    }

	    if (value == null) {
		System.out.println("not found");
	    } else {
		System.out.println("found");
		System.out.println(value);
	    }
	}

	@Override
	public String getInfo() {
	    return "obtains value by the key";
	}

	@Override
	public String getInvocation() {
	    return "<key>";
	}
    }

    static class Help implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    Iterator<Entry<String, Class<?>>> commands = shell.listCommands();

	    System.out
		    .println("MultiFileHashMap is an utility that lets you work with simple database");

	    System.out
		    .println(String
			    .format("You can set database directory to work with using environment variable '%s'",
				    Shell.DB_DIRECTORY_PROPERTY_NAME));

	    while (commands.hasNext()) {
		Entry<String, Class<?>> cmdEntry = commands.next();
		String cmdName = cmdEntry.getKey();
		Class<?> cmd = cmdEntry.getValue();

		try {
		    Command cmdInstance = (Command) cmd.newInstance();

		    System.out.println(String.format("\t%s%s\t%s", cmdName,
			    cmdInstance.getInvocation() == null ? ""
				    : (" " + cmdInstance.getInvocation()),
			    cmdInstance.getInfo()));
		} catch (Exception exc) {
		    Utility.handleError(exc,
			    String.format("%s: %s: cannot get method info",
				    args[0], cmdName), true);
		}
	    }
	}

	@Override
	public String getInfo() {
	    return "prints out description of shell commands";
	}

	@Override
	public String getInvocation() {
	    return null;
	}
    }

    static class List implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 1) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }
	    Set<String> keySet = null;

	    try {
		keySet = shell.getActiveTable().keySet();
	    } catch (DBFileCorruptException | IOException exc) {
		handleError(exc, String.format("Table %s is corrupt", shell
			.getActiveTable().getTableName()), true);
	    }
	    StringBuilder sb = new StringBuilder();

	    boolean comma = false;

	    for (String key : keySet) {
		sb.append(comma ? ", " : "").append(key);
		comma = true;
	    }

	    System.out.println(sb);
	}

	@Override
	public String getInfo() {
	    return "prints all keys stored in the map";
	}

	@Override
	public String getInvocation() {
	    return null;
	}
    }

    static class Put implements Command {

	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 3) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }
	    String key = args[1];
	    String value = args[2];

	    String oldValue = null;

	    try {
		oldValue = shell.getActiveTable().put(key, value);
	    } catch (DBFileCorruptException | IOException exc) {
		handleError(exc, String.format("Table %s is corrupt", shell
			.getActiveTable().getTableName()), true);
	    }

	    if (oldValue == null) {
		System.out.println("new");
	    } else {
		System.out.println("overwrite");
		System.out.println("old " + oldValue);
	    }
	}

	@Override
	public String getInfo() {
	    return "assigns new value to the key";
	}

	@Override
	public String getInvocation() {
	    return "<key> <value>";
	}
    }

    static class Remove implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }

	    String key = args[1];
	    String oldValue = null;

	    try {
		oldValue = shell.getActiveTable().remove(key);
	    } catch (DBFileCorruptException | IOException exc) {
		handleError(exc, String.format("Table %s is corrupt", shell
			.getActiveTable().getTableName()), true);
	    }

	    if (oldValue == null) {
		System.out.println("not found");
	    } else {
		System.out.println("removed");
	    }
	}

	@Override
	public String getInfo() {
	    return "removes value by the key";
	}

	@Override
	public String getInvocation() {
	    return "<key>";
	}
    }

    static class Show implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }

	    switch (args[1]) {
	    case "tables": {
		shell.getActiveDatabase().showTables();
		break;
	    }
	    default: {
		handleError("show: unexpected option: " + args[1]);
	    }
	    }
	}

	@Override
	public String getInfo() {
	    return "prints info on all tables assigned to the working database";
	}

	@Override
	public String getInvocation() {
	    return "tables";
	}
    }

    static class Use implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError(new WrongArgsNumberException(this), null, true);
	    }

	    shell.getActiveDatabase().useTable(args[1]);
	}

	@Override
	public String getInfo() {
	    return "saves all changes made to the current table (if present) and makes table with the given name the current one";
	}

	@Override
	public String getInvocation() {
	    return "<tablename>";
	}
    }
}
