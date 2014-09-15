package ru.fizteh.fivt.students.AndrewFedorov.shell;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for logging simple messages and exception cases.
 * 
 * @author phoenix
 * 
 */
public class Log {
    private Log() {
    }

    /**
     * If logging is disabled, no messages are output
     */
    private static boolean enableLogging = true;

    /**
     * Path to log file. By default - user.home
     */
    private final static Path logPath;

    /**
     * Writer to the log file.
     */
    private static PrintWriter writer;

    static {
	logPath = Paths.get(System.getProperty("user.home"), "java_shell.log");
	try {
	    writer = new PrintWriter(logPath.toAbsolutePath().toString());
	} catch (IOException exc) {
	    System.err.println(String.format("Cannot create log file: %s",
		    logPath));
	    System.err.println(exc.toString());
	    System.exit(1);
	}
    }

    public static void setEnableLogging(boolean enableLogging) {
	Log.enableLogging = enableLogging;
    }

    public static boolean isEnableLogging() {
	return enableLogging;
    }
    
    public static void close () {
	if (writer != null) {
	    writer.append("Log closing");
	    writer.close();
	    writer = null;
	}
    }

    public static void log(String message) {
	log(null, null, message);
    }

    public static void log(Class<?> logger, String message) {
	log(logger, null, message);
    }

    public static void log(Class<?> logger, Throwable throwable, String message) {
	if (enableLogging) {
	    StringBuilder sb = new StringBuilder(message == null ? 100
		    : message.length() * 2);

	    boolean appendSpace = false;

	    if (logger != null) {
		sb.append(logger.getSimpleName()).append(":");
		appendSpace = true;
	    }
	    if (message != null) {
		if (appendSpace) {
		    sb.append(" ");
		}
		sb.append(message);
		appendSpace = true;
	    }
	    if (throwable != null) {
		if (appendSpace) {
		    sb.append(" ");
		}
		sb.append(throwable.toString());
	    }
	    sb.append("\n");

	    if (throwable != null) {
		StackTraceElement[] trace = throwable.getStackTrace();
		for (int i = 0, len = trace.length; i < len; i++) {
		    StackTraceElement e = trace[i];
		    sb.append(String.format("\tat %s.%s in %s line %d\n",
			    e.getClassName(), e.getMethodName(),
			    e.getFileName(), e.getLineNumber()));
		}
	    }

	    writer.append(sb);
	    writer.flush();
	}
    }
}
