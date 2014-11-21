package ru.fizteh.fivt.students.RadimZulkarneev.JUnit;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.Command;
import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.Interpreter;
import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.InterpreterState;

public class InterpreterTest {
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private static final String PROMPT = "$ ";
    private static final String COMMAND_SEPARATOR = ";";
    private static final String testCommand = "test";
    private static final String testOutput = "TEST";
    private static final String newLine = System.getProperty("line.separator");

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void interpreterThrowsExceptionForNullStreamTest() {
        new Interpreter(null, new Command[]{}, null, null);
    }

    @Test
    public void interpreterInteractiveModeTest() {
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command("test", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                })}, new ByteArrayInputStream(
                    (testCommand + newLine + "exit" + newLine).getBytes()), printStream);
        interpreter.run(new String[] {});
        assertEquals(PROMPT + testOutput + newLine + PROMPT,
                outputStream.toString());
    }

    @Test
    public void testInterpreterRunInBatchMode() throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command("test", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                })}, new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {testCommand + COMMAND_SEPARATOR, testCommand});
        assertEquals(testOutput + newLine + testOutput + newLine, outputStream.toString());
    }


    @Test
    public void testInterpreterForCommandWithWrongNumberOfArguments()
            throws Exception {
        Interpreter interpreter = new Interpreter(null, new Command[] {
                new Command("test", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState testConnector, String[] arguments) {
                        printStream.println(testOutput);
                    }
                })}, new ByteArrayInputStream(new byte[] {}), printStream);
        interpreter.run(new String[] {testCommand + " some_argument"});
    }

}
