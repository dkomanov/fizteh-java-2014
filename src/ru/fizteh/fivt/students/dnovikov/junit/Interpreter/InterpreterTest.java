package ru.fizteh.fivt.students.dnovikov.junit.Interpreter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.dnovikov.junit.DataBaseProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class InterpreterTest {

    private final String lineSeparator = System.getProperty("line.separator");
    private final char commandSeparator = ';';
    private final String testCommand = "test";
    private final String testMessage = "test Message";
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    public DataBaseProvider provider;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream outputStreamError;
    private PrintStream printStream;
    private PrintStream printStreamError;

    @Before
    public void setUp() {
        provider = new DataBaseProvider(tmpFolder.getRoot().getPath());
        outputStream = new ByteArrayOutputStream();
        outputStreamError = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        printStreamError = new PrintStream(outputStreamError);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithNullArgumentsThrowsException() {
        new Interpreter(null, null, null, null, new Command[]{});
    }

    @Test
    public void runInterpreterInteractiveMode() throws IOException {

        Interpreter interpreter = new Interpreter(provider, new ByteArrayInputStream(
                (testCommand + commandSeparator + "exit").getBytes()), printStream, printStream, new Command[]{
                new Command("test", 0, new BiConsumer<DataBaseProvider, String[]>() {

                    @Override
                    public void accept(DataBaseProvider dataBaseProvider, String[] arguments) {
                        printStream.println(testMessage);
                    }
                })
        });
        interpreter.run(new String[]{});
        String actual = new String(outputStream.toString());
        String expected = new String("$ " + testMessage + lineSeparator);
        assertEquals(actual, expected);
    }

    @Test
    public void runInterpreterBatchMode() throws IOException {
        Interpreter interpreter = new Interpreter(provider, new ByteArrayInputStream(new byte[]{}),
                printStream, printStream, new Command[]{
                new Command("test", 0, new BiConsumer<DataBaseProvider, String[]>() {

                    @Override
                    public void accept(DataBaseProvider dataBaseProvider, String[] arguments) {
                        printStream.println(testMessage);
                    }
                })
        });
        String[] arguments = new String[]{testCommand};

        interpreter.run(arguments);
        String actual = new String(outputStream.toString());
        String expected = new String(testMessage + lineSeparator);
        assertEquals(actual, expected);
    }

    @Test
    public void runInterpreterInteractiveModeWithWrongNumberOfArguments() throws IOException {
        Interpreter interpreter = new Interpreter(provider, new ByteArrayInputStream(
                (testCommand + " argument" + lineSeparator + "exit").getBytes()), printStream, printStreamError,
                new Command[]{
                        new Command("test", 0, new BiConsumer<DataBaseProvider, String[]>() {

                            @Override
                            public void accept(DataBaseProvider dataBaseProvider, String[] arguments) {
                            }
                        })
                });
        interpreter.run(new String[]{});
        String actual = outputStreamError.toString();
        String expected = new String(testCommand + ": wrong number of arguments" + lineSeparator);
        assertEquals(actual, expected);
    }

    @Test
    public void runInterpreterInteractiveModeWithWrongCommand() throws IOException {
        Interpreter interpreter = new Interpreter(provider, new ByteArrayInputStream(
                ("wrongCommand" + lineSeparator + "exit").getBytes()), printStream, printStreamError, new Command[]{
                new Command("test", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseProvider, String[] arguments) {
                    }
                })
        });
        interpreter.run(new String[]{});
        String actual = outputStreamError.toString();
        String expected = new String("wrongCommand: no such command" + lineSeparator);
        assertEquals(actual, expected);
    }
}
