package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for logging simple messages and exception cases.
 * @author phoenix
 */
public class Log {
    /**
     * Path to log file. By default - user.home
     */
    private static final Path LOG_PATH =
            Paths.get(System.getProperty("user.home"), "java_multifilehashmap.log");
    /**
     * If logging is disabled, no messages are output
     */
    private static boolean enableLogging = true;
    /**
     * Writer to the log file.
     */
    private static PrintWriter writer;

    private static boolean firstOpen = true;

    private Log() {
    }

    private static void reopen() {
        try {
            writer = new PrintWriter(
                    new FileOutputStream(
                            LOG_PATH.toAbsolutePath().toString(), !firstOpen));
            firstOpen = false;
        } catch (IOException exc) {
            System.err.println(String.format("Cannot create log file: %s", LOG_PATH));
            System.err.println(exc.toString());
            System.exit(1);
        }
    }

    public static void close() {
        if (writer != null) {
            writer.println("Log closing");
            writer.close();
            writer = null;
        }
    }

    public static boolean isEnableLogging() {
        return enableLogging;
    }

    public static void setEnableLogging(boolean enableLogging) {
        Log.enableLogging = enableLogging;
    }

    public static void log(Class<?> logger, String message) {
        log(logger, null, message);
    }

    public static void log(Class<?> logger, Throwable throwable, String message) {
        if (writer == null) {
            reopen();
        }
        if (enableLogging) {
            StringBuilder sb = new StringBuilder(message == null ? 100 : message.length() * 2);

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

            while (throwable != null) {
                StackTraceElement[] trace = throwable.getStackTrace();
                for (int i = 0, len = trace.length; i < len; i++) {
                    StackTraceElement e = trace[i];
                    sb.append(
                            String.format(
                                    "\tat %s.%s in %s line %d\n",
                                    e.getClassName(),
                                    e.getMethodName(),
                                    e.getFileName(),
                                    e.getLineNumber()));
                }
                throwable = throwable.getCause();
                if (throwable != null) {
                    sb.append("\tcaused by:\n");
                }
            }

            writer.append(sb);
            writer.flush();
        }
    }

    public static void log(String message) {
        log(null, null, message);
    }
}
