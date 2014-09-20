package ru.fizteh.fivt.students.AndrewFedorov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that represents a shell that can execute commands like ls, cd, pwd, cp,
 * mv, rm, cat, exit.
 * 
 * @author phoenix
 * 
 */
public class Shell {
    public static void main(String[] args) {
	if (args.length == 0) {
	    new Shell().run(System.in);
	} else {
	    new Shell().run(args);
	}
    }

    private int readBufferSize = 10000; // 10 kb

    /**
     * Path to working directory
     */
    private Path workingDirectory;

    /**
     * Available commands for invocation.
     */
    private Map<String, Method> methodsMap;

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

	Method method = methodsMap.get(args[0]);
	if (method == null) {
	    Log.log(Shell.class,
		    String.format("Command not found by name %s", args[0]));
	    System.err.println("Sorry, this command is missing");
	    return false;
	} else {
	    try {
		method.invoke(null, this, (Object) args);
		return true;
	    } catch (Throwable exc) {
		if (!(exc.getCause() instanceof HandledException)) {
		    // unhandled exception
		    Log.log(Shell.class, exc, String.format(
			    "Error during execution of %s", args[0]));
		    System.err.println(String.format(
			    "%s: Method execution error", args[0]));
		}
		return false;
	    }
	}
    }

    public ShellCommand getCommandAnnotation(String methodName) {
	return methodsMap.get(methodName).getAnnotation(ShellCommand.class);
    }

    public int getReadBufferSize() {
	return readBufferSize;
    }

    public Iterator<String> getSupportedCommands() {
	return methodsMap.keySet().iterator();
    }

    public Path getWorkingDirectory() {
	return workingDirectory;
    }

    /**
     * Prepares shell for further command interpretation
     */
    private void init() {
	Log.log(Shell.class, "Shell starting");
	Method[] methods = Commands.class.getMethods();

	methodsMap = new HashMap<>(methods.length);
	for (int i = 0, len = methods.length; i < len; i++) {
	    if (methods[i].getAnnotation(ShellCommand.class) != null) {
		methodsMap.put(methods[i].getName(), methods[i]);
		Log.log(Shell.class,
			String.format("Method registered: %s",
				methods[i].getName()));
	    }
	}

	workingDirectory = Paths.get(System.getProperty("user.home"));
    }

    public boolean isInteractive() {
	return interactive;
    }

    /**
     * Execute commands from input stream. Commands are awaited forever.
     * 
     * @param stream
     */
    public void run(InputStream stream) {
	interactive = true;

	BufferedReader reader = new BufferedReader(
		new InputStreamReader(stream), getReadBufferSize());
	try {
	    while (true) {
		System.out.print(String.format("JShell: %s $ ",
			getWorkingDirectory()));

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

	Log.close();
    }

    public void setWorkingDirectory(Path workingDirectory) {
	this.workingDirectory = workingDirectory.normalize();
    }
}
