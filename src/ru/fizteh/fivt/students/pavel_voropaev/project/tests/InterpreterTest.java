package ru.fizteh.fivt.students.pavel_voropaev.project.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.Command;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.Interpreter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private final String newLine = System.getProperty("line.separator");
    private final String commandName = "testCommand";
    private final String command2Name = "testTableCommandWithException";
    private final String testOutput = "helloworld";
    private final String exitWarning = "Exit without saving!";
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    public class TestCommand extends AbstractCommand<Object> {
        public TestCommand(Object context, int argumentsNum) {
            super(commandName, argumentsNum, context);
        }

        @Override
        public void exec(String[] param, PrintStream out) {
            out.println(testOutput);
        }
    }

    public class TestTableCommandWithException extends AbstractCommand<Object> {
        public TestTableCommandWithException(Object context) {
            super(command2Name, 0, context);
        }

        @Override
        public void exec(String[] param, PrintStream out) {
            throw new InputMistakeException("exception");
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void interpreterInitializationForNullStream() {
        new Interpreter(new Command[]{}, null, null, null);
    }

    @Test
    public void interpreterInitialization() {
        new Interpreter(new Command[]{}, new ByteArrayInputStream(new byte[]{}), printStream, printStream);
    }

    @Test
    public void interpreterBatchModeCorrectCommandNoSaving() {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 0)},
                new ByteArrayInputStream(new byte[]{}), printStream, printStream);
        interpreter.run(new String[]{commandName + STATEMENT_DELIMITER + commandName});
        assertEquals(testOutput + newLine + testOutput + newLine + exitWarning + newLine, outputStream.toString());
    }

    @Test
    public void interpreterBatchModeCorrectCommandAndSaving() {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 0)},
                new ByteArrayInputStream(new byte[]{}), printStream, printStream);
        interpreter.run(new String[]{commandName + STATEMENT_DELIMITER + "exit"});
        assertEquals(testOutput + newLine, outputStream.toString());
    }

    @Test(expected = InputMistakeException.class)
    public void interpreterBatchModeWrongCommand() {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 0)},
                new ByteArrayInputStream(new byte[]{}), printStream, printStream);
        interpreter.run(new String[]{"wrong command"});
    }

    @Test
    public void interpreterInteractiveModeWithEOT() {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 0)},
                new ByteArrayInputStream(new byte[]{}), printStream, printStream);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + exitWarning + newLine, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeWithExit() {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 0)},
                new ByteArrayInputStream((commandName + newLine + "exit" + newLine).getBytes()),
                printStream, printStream);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + testOutput + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeNoSuchCommand() {
        Interpreter interpreter = new Interpreter(new Command[]{},
                new ByteArrayInputStream((commandName + newLine + "exit" + newLine).getBytes()),
                printStream, printStream);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + "No such command: " + commandName + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeTooManyArguments() {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 0)},
                new ByteArrayInputStream((commandName + " abcd" + newLine + "exit" + newLine).getBytes()),
                printStream, printStream);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + commandName + ": too many arguments" + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeNotEnoughArguments()  {
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(new Object(), 1)},
                new ByteArrayInputStream((commandName + newLine + "exit" + newLine).getBytes()),
                printStream, printStream);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + commandName + ": not enough arguments" + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeLaunchCommandWithMistake()  {
        Interpreter interpreter = new Interpreter(new Command[]{new TestTableCommandWithException(new Object())},
                new ByteArrayInputStream((command2Name + newLine + "exit" + newLine).getBytes()),
                printStream, printStream);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + "exception" + newLine + PROMPT, outputStream.toString());
    }
}
