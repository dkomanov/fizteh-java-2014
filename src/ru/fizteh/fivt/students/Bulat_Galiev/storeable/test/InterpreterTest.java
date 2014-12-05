package ru.fizteh.fivt.students.Bulat_Galiev.storeable.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.Bulat_Galiev.storeable.InterpreterPackage.Command;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.InterpreterPackage.Interpreter;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.InterpreterPackage.ExitException;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class InterpreterTest {
    private final String newLine = System.getProperty("line.separator");
    private final String testCommand = "test";
    private final String testOutput = "TEST";
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errputStream;
    private PrintStream printStream;
    private PrintStream errorStream;

    @Before
    public final void setUp() {
        outputStream = new ByteArrayOutputStream();
        errputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        errorStream = new PrintStream(errputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testInterpreterThrowsExceptionConstructedForNullStream()
            throws Exception {
        new Interpreter(null, new Command[] {}, null, null, null);
    }

    @Test
    public final void testInterpreterRunInInteractiveMode() throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider testConnector,
                                    final String[] arguments) {
                                printStream.println(testOutput);
                            }
                        }) }, new ByteArrayInputStream(
                        (testCommand + newLine).getBytes()), printStream,
                errorStream);
        try {
            interpreter.run(new String[] {});
        } catch (ExitException e) {
            assertEquals("$ " + testOutput + newLine + "$ ",
                    outputStream.toString());
        }
    }

    @Test
    public final void testInterpreterRunInBatchMode() throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider testConnector,
                                    final String[] arguments) {
                                printStream.println(testOutput);
                            }
                        }) }, new ByteArrayInputStream(new byte[] {}),
                printStream, errorStream);
        try {
            interpreter.run(new String[] {testCommand + ";", testCommand });
        } catch (ExitException e) {
            assertEquals(testOutput + newLine + testOutput + newLine,
                    outputStream.toString());
        }
    }

    @Test
    public final void testRunInterpreterInButchModeForUnexpectedCommand()
            throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {},
                new ByteArrayInputStream(new byte[] {}), printStream,
                errorStream);
        try {
            interpreter.run(new String[] {testCommand + ";", testCommand });
        } catch (ExitException e) {
            String expectedOutput = testCommand + " is incorrect command"
                    + newLine;
            assertEquals(expectedOutput, errputStream.toString());
        }
    }

    @Test
    public final void testRunInterpreterInInteractiveModeForUnexpectedCommand()
            throws Exception {
        String testInput = testCommand + newLine + testCommand;
        Interpreter interpreter = new Interpreter(null, new Command[] {},
                new ByteArrayInputStream(testInput.getBytes()), printStream,
                errorStream);
        try {
            interpreter.run(new String[] {});
        } catch (ExitException e) {
            String expectedOutput = testCommand + " is incorrect command"
                    + newLine + testCommand + " is incorrect command" + newLine;
            assertEquals(expectedOutput, errputStream.toString());
        }
    }

    @Test
    public final void testInterpreterForCommandWithWrongNumberOfArguments()
            throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider testConnector,
                                    final String[] arguments) {
                                printStream.println(testOutput);
                            }
                        }) }, new ByteArrayInputStream(new byte[] {}),
                printStream, errorStream);
        try {
            interpreter.run(new String[] {testCommand + " some_argument" });
        } catch (ExitException e) {
            String expectedOutput = testCommand
                    + ": Incorrect number of arguments: 0 expected, but 1 found."
                    + newLine;
            assertEquals(expectedOutput, errputStream.toString());
        }
    }

    @After
    public final void tearDown() throws IOException {
        outputStream.close();
        printStream.close();
        errputStream.close();
        errorStream.close();
    }
}
