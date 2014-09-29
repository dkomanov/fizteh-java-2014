package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Log {
    private static PrintStream writer;
    private static OutputStream stream;

    static {
        try {
            stream = new FileOutputStream("log.txt");
            try {
                writer = new PrintStream(stream, true);
            } catch (Throwable ex) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Throwable e) {
                        ex.addSuppressed(e);
                    }
                }
                throw ex;
            }
        } catch (Throwable ex) {
            System.err.println("Log error: " + ex.getMessage());
            closeStream();
        }
    }

    static void closeStream() {
        if (stream != null) {
            try {
                stream.close();
            } catch (Throwable ex) {
                System.err.println("Error closing log file: " + ex.getMessage());
            }
        }
    }


    static void println() {
        println("");
    }

    static void println(String line) {
        writer.println(line);
        writer.flush();
    }

    static void print(String line) {
        writer.print(line);
        writer.flush();
    }
}