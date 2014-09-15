package ru.fizteh.fivt.students.AndrewFedorov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private int readBufferSize = 10000; // 10 kb
    
    /**
     * Path to working directory
     */
    private Path workingDirectory;

    /**
     * Available commands for invocation.
     */
    private Map<String, Method> methodsMap;

    public Shell() {
	init();
    }

    /**
     * Execute commands from input stream. Commands are awaited forever.
     * 
     * @param stream
     */
    public void run(InputStream stream) {
	BufferedReader reader = new BufferedReader(
		new InputStreamReader(stream), getReadBufferSize());
	try {
	    while (true) {
		System.out.print(String.format("JShell: %s $ ", getWorkingDirectory()));
		
		String str = reader.readLine();
		String[] commands = str.split(";");
		for (int i = 0, len = commands.length; i < len; i++) {
		    execute(commands[i]);
		}
	    }
	} catch (IOException exc) {
	    Log.log(Shell.class, exc, "Cannot parse inputstream for shell");
	}
    }

    public int getReadBufferSize() {
	return readBufferSize;
    }

    public static void main(String[] args) {
	if (args.length == 0) {
	    new Shell().run(System.in);
	} else {
	    new Shell().run(args);
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
	StringBuilder sb = new StringBuilder();
	for (int i = 0, len = args.length; i < len; i++) {
	    sb.append((i == 0 ? "" : " ")).append(args[i]);
	}
	String cmds = sb.toString();
	String[] commands = cmds.split(";");

	for (int i = 0, len = commands.length; i < len; i++) {
	    execute(commands[i]);
	}
	
	Log.close();
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

    public Iterator<String> getSupportedCommands() {
	return methodsMap.keySet().iterator();
    }

    public ShellCommand getCommandAnnotation(String methodName) {
	return methodsMap.get(methodName).getAnnotation(ShellCommand.class);
    }

    public Path getWorkingDirectory() {
	return workingDirectory;
    }

    public void setWorkingDirectory(Path workingDirectory) {
	this.workingDirectory = workingDirectory.normalize();
    }

    /**
     * Executes command in this shell
     * 
     * @param command
     *            some shell command
     */
    public void execute(String command) {
	String[] args = command.trim().split(" ");
	Method method = methodsMap.get(args[0]);
	if (method == null) {
	    Log.log(Shell.class,
		    String.format("Command not found by name %s", args[0]));
	    System.err.println("Sorry, this command is missing");
	} else {
	    try {
		method.invoke(null, this, (Object) args);
	    } catch (IllegalArgumentException exc) {
		Log.log(Shell.class, exc, null);
		System.err.println(exc.getMessage());
	    } catch (Throwable exc) {
		Log.log(Shell.class, exc,
			String.format("Error during execution of %s", args[0]));
		System.err.println("Method execution error");
	    }
	}
    }
}
