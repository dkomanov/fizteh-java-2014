package ru.fizteh.fivt.students.AndrewFedorov.filemap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

//java -Ddb.file=/home/phoenix/test/db.dat ru.fizteh.fivt.students.AndrewFedorov.filemap.Shell

/**
 * Class that represents a shell that can execute commands defined as classes
 * that implement {@link Command} interface in {@link Commands}.
 * 
 * @author phoenix
 * @see Command
 * @see Commands
 */
public class Shell {
    public static final String DB_FILE_PROPERTY_NAME = "db.file";

    public static final int READ_BUFFER_SIZE = 16 * 1024;

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
    private Map<String, Class<?>> classesMap;

    private FileMap activeFileMap;

    /**
     * If the user is entering commands or it is package mode.
     */
    private boolean interactive;

    public Shell() {
    init();
    }

    /**
     * Executes command in this shell
     * 
     * @param command
     *            some shell command
     * @return returns true if execution finished correctly; false otherwise;
     */
    public boolean execute(String command) {
    String[] args = command.trim().split("[ ]{1,}");
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
     * Returns active file map object.<br/>
     * If no file map object in use {@link NullPointerException} is thrown.
     * 
     * @return active {@link FileMap} object.
     * @throws NullPointerException
     *             if active object is null
     */
    public FileMap getActiveFilemap() throws NullPointerException {
    if (activeFileMap == null) {
        throw new NullPointerException("No active file map in use");
    }
    return activeFileMap;
    }

    /**
     * Prepares shell for further command interpretation
     */
    private void init() {
    Log.log(Shell.class, "Shell starting");

    try {
        activeFileMap = new FileMap(
            System.getProperty(DB_FILE_PROPERTY_NAME));

        readDatabaseMap();
    } catch (HandledException exc) {
        System.exit(1);
    }

    // collecting commands
    Class<?>[] classes = Commands.class.getDeclaredClasses();
    classesMap = new HashMap<>(classes.length);

    for (int i = 0, len = classes.length; i < len; i++) {
        boolean isShellCommand = true;
        try {
        classes[i].asSubclass(Command.class);
        } catch (ClassCastException exc) {
        isShellCommand = false;
        }

        if (isShellCommand) {
        String simpleName = Utility.simplifyClassName(classes[i]
            .getSimpleName());

        classesMap.put(simpleName, classes[i]);
        Log.log(Shell.class, String.format(
            "Class registered: %s as '%s'", classes[i].getName(),
            simpleName));
        }
    }
    }

    public boolean isInteractive() {
    return interactive;
    }

    public Iterator<Entry<String, Class<?>>> listCommands() {
    return classesMap.entrySet().iterator();
    }

    /**
     * Reads database for the active file map from given file. Any exception is
     * wrapped into {@link HandledException}.
     * 
     * @throws HandledException
     */
    void readDatabaseMap() throws HandledException {
    try {
        getActiveFilemap().readDatabaseMap();
    } catch (Throwable exc) {
        if (exc instanceof HandledException) {
        throw (HandledException) exc;
        } else {
        Utility.handleError(exc, "Cannot read database from file", true);
        }
    }
    }

    /**
     * Execute commands from input stream. Commands are awaited until the-end-of-stream.
     * 
     * @param stream
     */
    public void run(InputStream stream) {
    interactive = true;

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(stream), READ_BUFFER_SIZE);
    try {
        while (true) {
        System.out.print(String.format("FileMap: %s $ ",
            getActiveFilemap().getDbFileName()));

        String str = reader.readLine();

        // end of stream
        if (str == null) {
            break;
        }

        // clone of the map before modifications
        FileMap fmapClone = activeFileMap.clone();

        String[] commands = str.split(";");
        for (int i = 0, len = commands.length; i < len; i++) {
            boolean correct = execute(commands[i]);
            if (!correct) {
            activeFileMap = fmapClone;
            try {
                activeFileMap.writeDatabaseMap();
            } catch (Exception exc) {
                System.exit(1);
            }
            break;
            }
        }
        }
    } catch (IOException exc) {
        Log.log(Shell.class, exc, "Cannot parse inputstream for shell");
    }

    try {
        writeDatabaseMap();
    } catch (HandledException exc) {
        System.exit(1);
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
        System.exit(1);
        }
    }

    try {
        writeDatabaseMap();
    } catch (HandledException exc) {
        System.exit(1);
    }
    Log.close();
    }

    /**
     * Sets active file map object.
     * 
     * @param fmap
     */
    public void setActiveFileMap(FileMap fmap) {
    activeFileMap = fmap;
    }

    /**
     * Writes database of the active file map to given file. Any exception is
     * wrapped into {@link HandledException}.
     * 
     * @throws HandledException
     */
    void writeDatabaseMap() throws HandledException {
    try {
        getActiveFilemap().writeDatabaseMap();
    } catch (Throwable exc) {
        if (exc instanceof HandledException) {
        throw (HandledException) exc;
        } else {
        Utility.handleError(exc, "Cannot write database to file", true);
        }
    }
    }

}
