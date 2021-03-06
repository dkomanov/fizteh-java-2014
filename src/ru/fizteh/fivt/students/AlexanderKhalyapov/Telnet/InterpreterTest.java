package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

public class InterpreterTest {
    private final String newLine = System.getProperty("line.separator");
    private final String testCommand = "test";
    private final String testOutput = "TEST";
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private BiConsumer<Object, String[]> printerCommand = (testConnector,
                                                           arguments) -> printStream.println(testOutput);

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpreterThrowsExceptionConstructedForNullStream() {
        new Interpreter(null, new Command[] {}, null, null);
    }

    @Test
    public void testInterpreterRunInInteractiveMode() throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0, printerCommand)},
                new ByteArrayInputStream(
                        (testCommand + newLine + "exit" + newLine).getBytes()),
                printStream);
        interpreter.run(new String[] {});
        assertEquals(Interpreter.PROMPT + testOutput + newLine
                + Interpreter.PROMPT, outputStream.toString());
    }

    @Test
    public void testInterpreterRunInBatchMode() throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0, printerCommand)},
                new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {
                testCommand + Interpreter.COMMAND_SEPARATOR, testCommand});
        assertEquals(testOutput + newLine + testOutput + newLine,
                outputStream.toString());
    }

    @Test
    public void testRunInterpreterInButchModeForUnexpectedCommand()
            throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {},
                new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {
                testCommand + Interpreter.COMMAND_SEPARATOR, testCommand});
        assertEquals(Interpreter.NO_SUCH_COMMAND_MSG + testCommand + newLine,
                outputStream.toString());
    }

    @Test
    public void testRunInterpreterInInteractiveModeForUnexpectedCommand()
            throws Exception {
        String testInput = testCommand + newLine + testCommand;
        Interpreter interpreter = new Interpreter(null, new Command[] {},
                new ByteArrayInputStream(testInput.getBytes()), printStream);
        interpreter.run(new String[] {});
        String expectedOutput = Interpreter.PROMPT
                + Interpreter.NO_SUCH_COMMAND_MSG + testCommand + newLine
                + Interpreter.PROMPT + Interpreter.NO_SUCH_COMMAND_MSG
                + testCommand + newLine + Interpreter.PROMPT;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInterpreterForCommandWithWrongNumberOfArguments()
            throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0, printerCommand)},
                new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {testCommand + " some_argument"});
    }
}
