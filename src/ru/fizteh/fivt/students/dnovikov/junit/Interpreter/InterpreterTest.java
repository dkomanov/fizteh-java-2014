package ru.fizteh.fivt.students.dnovikov.junit.Interpreter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.dnovikov.junit.DataBaseCommand;
import ru.fizteh.fivt.students.dnovikov.junit.DataBaseProvider;
import ru.fizteh.fivt.students.dnovikov.junit.DataBaseState;

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
    public DataBaseState state;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream outputStreamError;
    private PrintStream printStream;
    private PrintStream printStreamError;

    @Before
    public void setUp() throws IOException {
        provider = new DataBaseProvider(tmpFolder.getRoot().getPath());
        state = new DataBaseState(provider);
        outputStream = new ByteArrayOutputStream();
        outputStreamError = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        printStreamError = new PrintStream(outputStreamError);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithNullArgumentsThrowsException() {
        new Interpreter(null, null, null, null, new DataBaseCommand[]{});
    }

    @Test
    public void runInterpreterInteractiveMode() throws IOException {

        Interpreter interpreter = new Interpreter(state, new ByteArrayInputStream(
                (testCommand + commandSeparator + "exit").getBytes()), printStream, printStream, new DataBaseCommand[]{
                new DataBaseCommand("test", 0, new BiConsumer<InterpreterState, String[]>() {

                    @Override
                    public void accept(InterpreterState state, String[] arguments) {
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
        Interpreter interpreter = new Interpreter(state, new ByteArrayInputStream(new byte[]{}),
                printStream, printStream, new DataBaseCommand[]{
                new DataBaseCommand("test", 0, new BiConsumer<InterpreterState, String[]>() {

                    @Override
                    public void accept(InterpreterState state, String[] arguments) {
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
        Interpreter interpreter = new Interpreter(state, new ByteArrayInputStream(
                (testCommand + " argument" + lineSeparator + "exit").getBytes()), printStream, printStreamError,
                new DataBaseCommand[]{
                        new DataBaseCommand("test", 0, new BiConsumer<InterpreterState, String[]>() {

                            @Override
                            public void accept(InterpreterState state, String[] arguments) {
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
        Interpreter interpreter = new Interpreter(state, new ByteArrayInputStream(
                ("wrongCommand" + lineSeparator + "exit").getBytes()), printStream, printStreamError,
                new DataBaseCommand[]{new DataBaseCommand("test", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState state, String[] arguments) {
                    }
                })
                });
        interpreter.run(new String[]{});
        String actual = outputStreamError.toString();
        String expected = new String("wrongCommand: no such command" + lineSeparator);
        assertEquals(actual, expected);
    }
}
