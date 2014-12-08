package ru.fizteh.fivt.students.kolmakov_sergey.parallel.interpreter;

import static org.junit.Assert.assertEquals;

import ru.fizteh.fivt.students.kolmakov_sergey.parallel.data_base_structure.DataBaseState;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

public class InterpreterTest {
    private final String newLine = System.getProperty("line.separator");
    private final String testCommandName = "test";
    private final String testOutput = "TEST";
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    @Before
    public void setUp() {
        System.out.println(newLine);
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpreterThrowsExceptionConstructedForNullStream() {
        new Interpreter(new Command[] {}, null, null);
    }

    @Test
    public void testTryUseInterpreterWithoutExitHandler() throws Exception {
        Interpreter interpreter = new Interpreter(new Command[] {
                new Command(testCommandName, 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState state, String[] arguments) {
                    }
                }, null)}, new ByteArrayInputStream((testCommandName + newLine).getBytes()), printStream);
        interpreter.run(new String[]{"exit"});
        assertEquals("", outputStream.toString());
    }

    @Test
    public void testInterpreterRunInInteractiveMode() throws Exception {
        Interpreter interpreter = new Interpreter(new Command[] {
                new Command("test", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                }, null)}, new ByteArrayInputStream((testCommandName + newLine +
                "exit" + newLine).getBytes()), printStream);
        interpreter.run(new String[] {});
        assertEquals(Interpreter.PROMPT + testOutput + newLine + Interpreter.PROMPT, outputStream.toString());
    }

    @Test
    public void testInterpreterRunInBatchMode() throws Exception {
        Interpreter interpreter = new Interpreter(new Command[] {
                new Command("test", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                }, null)}, new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {testCommandName + Interpreter.STATEMENT_DELIMITER, testCommandName});
        assertEquals(testOutput + newLine + testOutput + newLine, outputStream.toString());
    }

    @Test
    public void testRunInterpreterInButchModeForUnexpectedCommand() throws Exception {
        Interpreter interpreter = new Interpreter(new Command[] {},
                new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {testCommandName + Interpreter.STATEMENT_DELIMITER, testCommandName});
        assertEquals(Interpreter.BAD_COMMAND + testCommandName + newLine, outputStream.toString());
    }

    @Test
    public void testRunInterpreterInInteractiveModeForUnexpectedCommand()
            throws Exception {
        String testInput = testCommandName + newLine + testCommandName + newLine + "exit";
        Interpreter interpreter = new Interpreter(new Command[] {},
                new ByteArrayInputStream(testInput.getBytes()), printStream);
        interpreter.run(new String[] {});
        String expectedOutput
                = Interpreter.PROMPT + Interpreter.BAD_COMMAND + testCommandName + newLine
                + Interpreter.PROMPT + Interpreter.BAD_COMMAND + testCommandName + newLine
                + Interpreter.PROMPT;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInterpreterForCommandWithWrongNumberOfArguments()
            throws Exception {
        Interpreter interpreter = new Interpreter(new Command[] {
                new Command("test", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                }, null)}, new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {testCommandName + " unexpected"});
        assertEquals(testCommandName + ": incorrect number of arguments" + newLine, outputStream.toString());
    }
}
