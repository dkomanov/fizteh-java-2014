package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

import static ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Utility.*;

import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.DatabaseException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.TerminalException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.AccurateAction;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.AccurateExceptionHandler;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Utility;

public class Commands {
    static class Create extends AbstractCommand {
	public Create() {
	    super("<tablename>", "creates a new table with the given name", 2);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {

		@Override
		public void perform() throws Exception {
		    shell.getActiveDatabase().createTable(args[1]);
		}
	    }, databaseErrorHandler, shell);

	}
    }

    static class Drop extends AbstractCommand {
	public Drop() {
	    super("<tablename>",
		  "deletes table with the given name from file system",
		  2);
	}

	@Override
	public void executeAfterChecking(Shell shell, String[] args)
		throws TerminalException {
	    shell.getActiveDatabase().dropTable(args[1]);
	}
    }

    static class Exit extends AbstractCommand {
	public Exit() {
	    super(null,
		  "saves all data to file system and stops interpretation",
		  1);
	}

	@Override
	public void executeAfterChecking(Shell shell, String[] args)
		throws TerminalException {
	    shell.persistDatabase();
	    shell.exit(0);
	}
    }

    static class Get extends AbstractCommand {
	public Get() {
	    super("<key", "obtains value by the key", 2);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {
		@Override
		public void perform() throws Exception {
		    String key = args[1];

		    String value = shell.getActiveTable().get(key);

		    if (value == null) {
			System.out.println("not found");
		    } else {
			System.out.println("found");
			System.out.println(value);
		    }
		}
	    }, databaseErrorHandler, shell);
	}
    }

    static class Help extends AbstractCommand {
	public Help() {
	    // If someone needs help, no matter which arguments are given
	    super(null,
		  "prints out description of shell commands",
		  1,
		  Integer.MAX_VALUE);
	}

	@Override
	public void execute(Shell shell, String[] args)
		throws TerminalException {
	    Set<Entry<String, Class<? extends Command>>> commands = shell.listCommands();

	    System.out.println("MultiFileHashMap is an utility that lets you work with simple database");

	    System.out.println(String.format("You can set database directory to work with using environment variable '%s'",
					     Shell.DB_DIRECTORY_PROPERTY_NAME));

	    for (Entry<String, Class<? extends Command>> cmdEntry : commands) {
		String cmdName = cmdEntry.getKey();
		Class<?> cmd = cmdEntry.getValue();

		try {
		    Command cmdInstance = (Command) cmd.newInstance();

		    System.out.println(String.format("\t%s%s\t%s",
						     cmdName,
						     cmdInstance.getInvocation() == null
							     ? ""
							     : (" " + cmdInstance.getInvocation()),
						     cmdInstance.getInfo()));
		} catch (Exception exc) {
		    Utility.handleError(exc,
					String.format("%s: %s: cannot get method info",
						      args[0],
						      cmdName),
					true);
		}
	    }
	}

	@Override
	public void executeAfterChecking(Shell shell, String[] args)
		throws TerminalException {
	}
    }

    static class List extends AbstractCommand {
	public List() {
	    super(null, "prints all keys stored in the map", 1);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {

		@Override
		public void perform() throws Exception {
		    Set<String> keySet = shell.getActiveTable().keySet();
		    StringBuilder sb = new StringBuilder();

		    boolean comma = false;

		    for (String key : keySet) {
			sb.append(comma ? ", " : "").append(key);
			comma = true;
		    }

		    System.out.println(sb);
		}
	    }, databaseErrorHandler, shell);
	}
    }

    static class Put extends AbstractCommand {
	public Put() {
	    super("<key> <value>", "assigns new value to the key", 3);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {
		@Override
		public void perform() throws Exception {
		    String key = args[1];
		    String value = args[2];

		    String oldValue = shell.getActiveTable().put(key, value);

		    if (oldValue == null) {
			System.out.println("new");
		    } else {
			System.out.println("overwrite");
			System.out.println("old " + oldValue);
		    }
		}
	    }, databaseErrorHandler, shell);
	}
    }

    static class Remove extends AbstractCommand {
	public Remove() {
	    super("<key>", "removes value by the key", 2);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {

		@Override
		public void perform() throws Exception {
		    String key = args[1];
		    String oldValue = shell.getActiveTable().remove(key);

		    if (oldValue == null) {
			System.out.println("not found");
		    } else {
			System.out.println("removed");
		    }
		}
	    }, databaseErrorHandler, shell);
	}
    }

    static class Show extends AbstractCommand {
	public Show() {
	    super("tables",
		  "prints info on all tables assigned to the working database",
		  2);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {

		@Override
		public void perform() throws Exception {
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
	    }, databaseErrorHandler, shell);
	}
    }

    static class Use extends AbstractCommand {
	public Use() {
	    super("<tablename>",
		  "saves all changes made to the current table (if present) and makes table with the given name the current one",
		  2);
	}

	@Override
	public void executeAfterChecking(final Shell shell, final String[] args)
		throws TerminalException {
	    Utility.performAccurately(new AccurateAction() {

		@Override
		public void perform() throws Exception {
		    shell.getActiveDatabase().useTable(args[1]);
		}
	    }, databaseErrorHandler, shell);
	}
    }

    /**
     * Used for unsafe calls. Catches all extensions of
     * {@link DatabaseException }.
     */
    public final static AccurateExceptionHandler<Shell> databaseErrorHandler = new AccurateExceptionHandler<Shell>() {

	@Override
	public void handleException(Exception exc, Shell shell)
		throws TerminalException {
	    if (exc instanceof DatabaseException || exc instanceof IllegalArgumentException) {
		handleError(exc, exc.getMessage(), true);
	    } else if (exc instanceof RuntimeException) {
		throw (RuntimeException) exc;
	    } else {
		throw new RuntimeException("Unexpected exception", exc);
	    }
	}

    };
}
