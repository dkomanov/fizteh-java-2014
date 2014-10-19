package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.Database;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.TableImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.HandledException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

//java -Dfizteh.db.dir=/home/phoenix/test/DB ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.Shell

/**
 * Class that represents a shell that can execute commands defined as classes
 * that implement {@link Command} interface in {@link Commands}.
 * 
 * @author phoenix
 * @see Command
 * @see Commands
 */
public class Shell {
    public final static String DB_DIRECTORY_PROPERTY_NAME = "fizteh.db.dir";

    public final static int READ_BUFFER_SIZE = 16 * 1024;

    public static void main(String[] args) {
	if (args.length == 0) {
	    new Shell().run(System.in);
	} else {
	    new Shell().run(args);
	}
    }

    /**
     * Available commands for invocation.
     */
    private Map<String, Class<? extends Command>> classesMap;

    private Database activeDatabase;

    /**
     * If the user is entering commands or it is package mode.
     */
    private boolean interactive;

    public Shell() {
	init();
    }

    /**
     * Do some minor optimizations - delete empty files and directories.
     */
    public void cleanup() {
	// Delete empty files and directories inside tables' directories
	Path dbDirectory = (activeDatabase == null ? null : activeDatabase
		.getDbDirectory());

	if (dbDirectory != null) {
	    try (DirectoryStream<Path> dirStream = Files
		    .newDirectoryStream(dbDirectory)) {
		for (Path tableDirectory : dirStream) {
		    Utility.removeEmptyFilesAndFolders(tableDirectory);
		}

		Log.log(Shell.class, "Cleaned up successfully");
	    } catch (IOException exc) {
		Log.log(Shell.class, exc, "Failed to clean up");
	    }
	}
    }

    /**
     * Executes command in this shell
     * 
     * @param command
     *            some shell command
     * @return returns true if execution finished correctly; false otherwise;
     */
    public boolean execute(String command) {
	String[] args = command.trim().split("[ \t]{1,}");
	if (args[0].isEmpty()) {
	    return true;
	}

	Log.log(Shell.class, "Invocation request: " + Arrays.toString(args));

	Class<?> commandClass = classesMap.get(args[0]);
	if (commandClass == null) {
	    Log.log(Shell.class,
		    String.format("Command not found by name %s", args[0]));
	    System.err.println("Sorry, this command is missing");
	    return false;
	} else {
	    try {
		Command commandInstance = (Command) commandClass.newInstance();
		commandInstance.execute(this, args);

		return true;
	    } catch (Throwable exc) {
		if (!(exc instanceof HandledException)) {
		    // unhandled exception
		    Log.log(Shell.class, exc, String.format(
			    "Error during execution of %s", args[0]));
		    System.err.println(String.format(
			    "%s: Method execution error", args[0]));
		}
		// throw new RuntimeException(exc);
		return false;
	    }
	}
    }

    /**
     * Call this method to clean up properly and exit then.
     * 
     * @param code
     */
    public void exit(int code) {
	cleanup();

	Log.close();
	System.exit(code);
    }

    public Database getActiveDatabase() {
	return activeDatabase;
    }

    /**
     * Prepares shell for further command interpretation
     */
    private void init() {
	Log.log(Shell.class, "Shell starting");

	try {
	    String dbDirPath = System.getProperty(DB_DIRECTORY_PROPERTY_NAME);
	    if (dbDirPath == null) {
		Utility.handleError("Please mention database directory");
	    }

	    activeDatabase = Database.establishDatabase(Paths.get(dbDirPath));
	} catch (IllegalArgumentException | DatabaseException exc) {
	    try {
		Utility.handleError(exc, exc.getMessage(), true);
	    } catch (HandledException inner) {
		this.exit(1);
	    }
	} catch (HandledException exc) {
	    this.exit(1);
	}

	// collecting commands
	Class<?>[] classes = Commands.class.getDeclaredClasses();
	classesMap = new HashMap<>(classes.length);

	for (int i = 0, len = classes.length; i < len; i++) {
	    Class<? extends Command> commandClass = null;

	    try {
		commandClass = classes[i].asSubclass(Command.class);
	    } catch (ClassCastException exc) {
	    }

	    if (commandClass != null) {
		String simpleName = Utility.simplifyClassName(classes[i]
			.getSimpleName());

		classesMap.put(simpleName, commandClass);
		Log.log(Shell.class, String.format(
			"Class registered: %s as '%s'", classes[i].getName(),
			simpleName));
	    }
	}
    }

    public boolean isInteractive() {
	return interactive;
    }

    public Set<Entry<String, Class<? extends Command>>> listCommands() {
	return classesMap.entrySet();
    }

    /**
     * Persists the current database.
     * 
     * @throws HandledException
     *             all errors are wrapped into this.
     */
    public void persistDatabase() throws HandledException {
	try {
	    activeDatabase.commit();
	} catch (DatabaseException exc) {
	    Utility.handleError(exc, exc.getMessage(), true);
	}
    }

    /**
     * Execute commands from input stream. Commands are awaited until
     * the-end-of-stream.
     * 
     * @param stream
     */
    public void run(InputStream stream) {
	interactive = true;

	BufferedReader reader = new BufferedReader(
		new InputStreamReader(stream), READ_BUFFER_SIZE);
	try {
	    while (true) {
		TableImpl table = null;
		try {
		    table = activeDatabase.getActiveTable();
		} catch (NoActiveTableException exc) {
		}
		System.out.println(String.format("%s%s $ ", (table == null ? ""
			: (table.getName() + "@")), activeDatabase
			.getDbDirectory()));

		String str = reader.readLine();

		// end of stream
		if (str == null) {
		    break;
		}

		String[] commands = str.split(";");
		for (int i = 0, len = commands.length; i < len; i++) {
		    boolean correct = execute(commands[i]);
		    if (!correct) {
			break;
		    }
		}
	    }
	} catch (IOException exc) {
	    Log.log(Shell.class, exc, "Cannot parse inputstream for shell");
	}

	try {
	    persistDatabase();
	} catch (HandledException exc) {
	    this.exit(1);
	}
    }

    /**
     * Execute commands from command line arguments. Note that command line
     * arguments are first concatenated into a single line then split and
     * parsed.
     * 
     * @param args
     */
    public void run(String[] args) {
	interactive = false;

	StringBuilder sb = new StringBuilder();
	for (int i = 0, len = args.length; i < len; i++) {
	    sb.append((i == 0 ? "" : " ")).append(args[i]);
	}
	String cmds = sb.toString();
	String[] commands = cmds.split(";");

	for (int i = 0, len = commands.length; i < len; i++) {
	    boolean correct = execute(commands[i]);
	    if (!correct) {
		this.exit(1);
	    }
	}

	try {
	    persistDatabase();
	} catch (HandledException exc) {
	    this.exit(1);
	}
	this.exit(0);
    }
}
