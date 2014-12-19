package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.DataBase;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.Interpreter;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands.Command;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands.DatabaseCommand;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.ExitException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class InterpreterTest {
    private static String newLine = System.getProperty("line.separator");
    private static String testCommand = "command";
    private static String testOutput = "TEST";
    private static BiConsumer<DataBase, String[]> testConsumer;
    private static BiConsumer<DataBase, String[]> emptyConsumer;
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        testConsumer = (object, arguments) -> printStream.println(testOutput);
        emptyConsumer = (object, arguments) -> {
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpreterThrowsIllegalArgumentExceptionIfGivenNullStreams() {
        new Interpreter(new Command[]{}, null, null);
    }

    @Test
    public void testRunValidUserMode() {
        Interpreter test = new Interpreter(new Command[]{
                new DatabaseCommand(null, "command", 1, testConsumer)},
                new ByteArrayInputStream(
                        (testCommand + newLine).getBytes()), printStream);
        try {
            test.run(new String[]{});
        } catch (ExitException e) {

            assertEquals(0, e.getStatus());

            assertEquals(Interpreter.PROMPT + testOutput + newLine + Interpreter.PROMPT,
                    outputStream.toString());
        }

    }

    @Test
    public void testRunValidBatchMode() {
        Interpreter test = new Interpreter(new Command[]{
                new DatabaseCommand(null, "command", 1, testConsumer),
                new DatabaseCommand(null, Interpreter.EXIT_COMMAND, 1, emptyConsumer)},
                new ByteArrayInputStream(new byte[]{}), printStream);
        try {
            test.run(new String[]{testCommand + Interpreter.STATEMENT_DELIMITER, testCommand});
        } catch (ExitException e) {

            assertEquals(0, e.getStatus());

            assertEquals(testOutput + newLine + testOutput + newLine, outputStream.toString());
        }
    }

    @Test
    public void testBatchModePrintErrorMessageInErrStreamForUnexpectedCommand() {
        Interpreter test = new Interpreter(new Command[]{},
                new ByteArrayInputStream(new byte[]{}), printStream);
        try {
            test.run(new String[]{testCommand});
        } catch (ExitException e) {

            assertNotEquals(0, e.getStatus());

            assertEquals(Interpreter.COMMAND_NOT_FOUNG_MSG + testCommand + newLine, outputStream.toString());
        }
    }

    @Test
    public void testUserModeWriteErrorInStreamForUnexpectedCommand() {
        Interpreter test = new Interpreter(new Command[]{},
                new ByteArrayInputStream((testCommand + newLine).getBytes()), printStream);
        try {
            test.run(new String[]{});
        } catch (ExitException e) {

            assertEquals(0, e.getStatus());

            assertEquals(Interpreter.PROMPT + Interpreter.COMMAND_NOT_FOUNG_MSG
                    + testCommand + newLine + Interpreter.PROMPT, outputStream.toString());
        }
    }

    @Test
    public void testBatchModePrintErrorMessageInErrStreamForCommandWithWrongNumberOfArguments() {
        Interpreter test = new Interpreter(new Command[]{
                new DatabaseCommand(null, "command", 1, testConsumer)},
                new ByteArrayInputStream(new byte[]{}), printStream);
        try {
            test.run(new String[]{testCommand + " argument"});
        } catch (ExitException e) {

            assertEquals("command: invalid number of arguments: 0 expected, 1 found."
                    + newLine, outputStream.toString());

            assertNotEquals(0, e.getStatus());
        }
    }

    @Test
    public void testUserModePrintErrorMessageInOutStreamForCommandWithWrongNumberOfArguments() {
        Interpreter test = new Interpreter(new Command[]{
                new DatabaseCommand(null, "command", 1, testConsumer)},
                new ByteArrayInputStream((testCommand + " argument").getBytes()), printStream);
        try {
            test.run(new String[]{});
        } catch (ExitException e) {

            assertEquals(Interpreter.PROMPT
                            + "command: invalid number of arguments: 0 expected, 1 found."
                            + newLine + Interpreter.PROMPT,
                    outputStream.toString());
        }
    }

    @After
    public void tearDown() throws IOException {
        outputStream.close();
        printStream.close();
    }

}
