package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.interpreter;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure.DataBaseState;

public class InterpreterTest {
    private final String newLine = System.getProperty("line.separator");
    private final String testCommandName = "test";
    private final String testOutput = "TEST";
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    private Callable<Boolean> makeFakeHandler() {  // For tests.
        Callable<Boolean> exitHandler = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        };
        return exitHandler;
    }

    @Before
    public void setUp() {
        System.out.println(newLine);
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpreterThrowsExceptionConstructedForNullStream() {
        new Interpreter(null, new Command[] {}, null, null);
    }

    @Test
    public void testTryUseInterpreterWithoutExitHandler() throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command(testCommandName, 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState state, String[] arguments) {
                    }
                })}, new ByteArrayInputStream((testCommandName + newLine).getBytes()), printStream);
        interpreter.run(new String[]{"exit"});
        assertEquals(Interpreter.EXIT_WITHOUT_HANDLER_MSG + newLine, outputStream.toString());
    }

    @Test
    public void testInterpreterRunInInteractiveMode() throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command("test", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                })}, new ByteArrayInputStream((testCommandName + newLine + "exit" + newLine).getBytes()), printStream);
        interpreter.setExitHandler(makeFakeHandler());
        interpreter.run(new String[] {});
        assertEquals(Interpreter.PROMPT + testOutput + newLine + Interpreter.PROMPT, outputStream.toString());
    }


    @Test
    public void testInterpreterRunInBatchMode() throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command("test", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                })}, new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.setExitHandler(makeFakeHandler());
        interpreter.run(new String[] {testCommandName + Interpreter.STATEMENT_DELIMITER, testCommandName});
        assertEquals(testOutput + newLine + testOutput + newLine, outputStream.toString());
    }

    @Test
    public void testRunInterpreterInButchModeForUnexpectedCommand() throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {},
                new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.setExitHandler(makeFakeHandler());
        interpreter.run(new String[] {testCommandName + Interpreter.STATEMENT_DELIMITER, testCommandName});
        assertEquals(Interpreter.BAD_COMMAND + testCommandName + newLine, outputStream.toString());
    }

    @Test
    public void testRunInterpreterInInteractiveModeForUnexpectedCommand()
            throws Exception {
        String testInput = testCommandName + newLine + testCommandName + newLine + "exit";
        Interpreter interpreter = new Interpreter(null, new Command[] {},
                new ByteArrayInputStream(testInput.getBytes()), printStream);
        interpreter.setExitHandler(makeFakeHandler());
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
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command("test", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                    @Override
                    public void accept(DataBaseState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                })}, new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.setExitHandler(makeFakeHandler());
        interpreter.run(new String[] {testCommandName + " unexpected"});
        assertEquals(testCommandName + ": incorrect number of arguments" + newLine, outputStream.toString());
    }
}