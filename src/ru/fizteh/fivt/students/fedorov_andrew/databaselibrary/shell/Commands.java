package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility.*;

import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.HandledException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

public class Commands {
    static class Commit extends AbstractCommand implements Command {
	public Commit() {
	    super(null, "saves all changes made from the last commit", 1);
	}

	@Override
	public void executeSafely(Shell shell, String[] args)
		throws DatabaseException, IllegalArgumentException {
	    int changes = shell.getActiveDatabase().commit();
	    System.out.println(changes);
	}
    }

    static class Rollback extends AbstractCommand implements Command {
	public Rollback() {
	    super(null, "cancels all changes made from the last commit", 1);
	}

	@Override
	public void executeSafely(Shell shell, String[] args)
		throws DatabaseException, IllegalArgumentException {
	    int changes = shell.getActiveDatabase().rollback();
	    System.out.println(changes);
	}
    }

    static class Size extends AbstractCommand implements Command {
	public Size() {
	    super(null, "prints count of stored keys in current table", 1);
	}

	@Override
	public void executeSafely(Shell shell, String[] args)
		throws DatabaseException, IllegalArgumentException {
	    int size = shell.getActiveDatabase().getActiveTable().size();
	    System.out.println(size);
	}
    }

    static class Create extends AbstractCommand implements Command {
	public Create() {
	    super("<tablename>", "creates a new table with the given name", 2);
	}

	@Override
	public void executeSafely(Shell shell, String[] args) {
	    boolean created = shell.getActiveDatabase().createTable(args[1]);
	    if (created) {
		System.out.println("created");
	    } else {
		throw new DatabaseException("Table " + args[1] + " exists");
	    }
	}
    }

    static class Drop extends AbstractCommand implements Command {
	public Drop() {
	    super("<tablename>",
		    "deletes table with the given name from file system", 2);
	}

	@Override
	public void executeSafely(final Shell shell, final String[] args) {
	    shell.getActiveDatabase().dropTable(args[1]);
	    System.out.println("dropped");
	}
    }

    static class Exit extends AbstractCommand implements Command {
	public Exit() {
	    super(null,
		    "saves all data to file system and stops interpretation", 1);
	}

	@Override
	public void executeSafely(Shell shell, String[] args) {
	    shell.persistDatabase();
	    shell.exit(0);
	}
    }

    static class Get extends AbstractCommand implements Command {
	public Get() {
	    super("<key>", "obtains value by the key", 2);
	}

	@Override
	public void executeSafely(final Shell shell, final String[] args) {
	    String key = args[1];

	    String value = shell.getActiveDatabase().getActiveTable().get(key);

	    if (value == null) {
		System.out.println("not found");
	    } else {
		System.out.println("found");
		System.out.println(value);
	    }
	}
    }

    static class Help extends AbstractCommand implements Command {
	public Help() {
	    // If someone needs help, no matter which arguments are given
	    super(null, "prints out description of shell commands", 1,
		    Integer.MAX_VALUE);
	}

	@Override
	public void executeSafely(Shell shell, String[] args)
		throws HandledException {
	    // not used
	}

	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    Set<Entry<String, Class<? extends Command>>> commands = shell
		    .listCommands();

	    System.out
		    .println("MultiFileHashMap is an utility that lets you work with simple database");

	    System.out
		    .println(String
			    .format("You can set database directory to work with using environment variable '%s'",
				    Shell.DB_DIRECTORY_PROPERTY_NAME));

	    for (Entry<String, Class<? extends Command>> cmdEntry : commands) {
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
    }

    static class List extends AbstractCommand implements Command {
	public List() {
	    super(null, "prints all keys stored in the map", 1);
	}

	@Override
	public void executeSafely(final Shell shell, final String[] args) {
	    java.util.List<String> keySet = shell.getActiveDatabase()
		    .getActiveTable().list();
	    StringBuilder sb = new StringBuilder();

	    boolean comma = false;

	    for (String key : keySet) {
		sb.append(comma ? ", " : "").append(key);
		comma = true;
	    }

	    System.out.println(sb);
	}
    }

    static class Put extends AbstractCommand implements Command {
	public Put() {
	    super("<key> <value>", "assigns new value to the key", 3);
	}

	@Override
	public void executeSafely(final Shell shell, final String[] args) {
	    String key = args[1];
	    String value = args[2];

	    String oldValue = shell.getActiveDatabase().getActiveTable()
		    .put(key, value);

	    if (oldValue == null) {
		System.out.println("new");
	    } else {
		System.out.println("overwrite");
		System.out.println("old " + oldValue);
	    }
	}
    }

    static class Remove extends AbstractCommand implements Command {
	public Remove() {
	    super("<key>", "removes value by the key", 2);
	}

	@Override
	public void executeSafely(final Shell shell, final String[] args) {
	    String key = args[1];
	    String oldValue = shell.getActiveDatabase().getActiveTable()
		    .remove(key);

	    if (oldValue == null) {
		System.out.println("not found");
	    } else {
		System.out.println("removed");
	    }
	}
    }

    static class Show extends AbstractCommand implements Command {
	public Show() {
	    super(
		    "tables",
		    "prints info on all tables assigned to the working database",
		    2);
	}

	@Override
	public void executeSafely(Shell shell, String[] args) {
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
    }

    static class Use extends AbstractCommand implements Command {
	public Use() {
	    super(
		    "<tablename>",
		    "saves all changes made to the current table (if present) and makes table with the given name the current one",
		    2);
	}

	@Override
	public void executeSafely(final Shell shell, final String[] args)
		throws HandledException {
	    shell.getActiveDatabase().useTable(args[1]);
	}
    }
}
