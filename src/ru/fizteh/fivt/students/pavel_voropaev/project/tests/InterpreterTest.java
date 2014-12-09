package ru.fizteh.fivt.students.pavel_voropaev.project.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.Command;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.Interpreter;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.InterpreterState;

import java.io.*;

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

    private class TestCommand extends AbstractCommand {
        public TestCommand(InterpreterState state, int argumentsNumber) {
            super(commandName, argumentsNumber, state);
        }

        @Override
        public void exec(String[] param) {
            state.getOutputStream().println(testOutput);
        }
    }

    private class TestTableCommandWithException extends AbstractCommand {
        public TestTableCommandWithException(InterpreterState state, int argumentsNumber) {
            super(command2Name, argumentsNumber, state);
        }

        @Override
        public void exec(String[] param) {
            throw new InputMistakeException("exception");
        }
    }

    private class TestInterpreterState implements InterpreterState {
        private InputStream in;
        private PrintStream out;
        private PrintStream err;
        private boolean exitSafeAnswer;

        public TestInterpreterState(InputStream in, PrintStream out, PrintStream err, boolean exitSafeAnswer) {
            this.in = in;
            this.out = out;
            this.err = err;
            this.exitSafeAnswer = exitSafeAnswer;
        }

        @Override
        public InputStream getInputStream() {
            return in;
        }

        @Override
        public PrintStream getOutputStream() {
            return out;
        }

        @Override
        public PrintStream getErrorStream() {
            return err;
        }

        @Override
        public boolean isExitSafe() {
            return exitSafeAnswer;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void interpreterInitializationForNullStream() {
        InterpreterState state = new TestInterpreterState(null, null, null, true);
        new Interpreter(new Command[]{}, state);
    }

    @Test
    public void interpreterInteractiveModeSaving() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream(new byte[]{}), printStream,
                printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeWithoutSaving() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream(new byte[]{}), printStream,
                printStream, false);
        Interpreter interpreter = new Interpreter(new Command[]{}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + exitWarning + newLine, outputStream.toString());
    }

    @Test
    public void interpreterBatchModeCorrectCommandNoSaving() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream(new byte[]{}), printStream,
                printStream, false);
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(state, 0)}, state);

        interpreter.run(new String[]{commandName + STATEMENT_DELIMITER + commandName});
        assertEquals(testOutput + newLine + testOutput + newLine + exitWarning + newLine, outputStream.toString());
    }

    @Test
    public void interpreterBatchModeCorrectCommandWithSaving() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream(new byte[]{}), printStream,
                printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(state, 0)}, state);

        interpreter.run(new String[]{commandName + STATEMENT_DELIMITER + commandName});
        assertEquals(testOutput + newLine + testOutput + newLine, outputStream.toString());
    }

    @Test(expected = InputMistakeException.class)
    public void interpreterBatchModeWrongCommand() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream(new byte[]{}), printStream,
                printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{}, state);

        interpreter.run(new String[]{"wrong command"});
    }

    @Test
    public void interpreterInteractiveModeNoSuchCommand() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream((commandName + newLine).getBytes()),
                printStream, printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + "No such command: " + commandName + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeTooManyArguments() throws IOException {
        InterpreterState state = new TestInterpreterState(
                new ByteArrayInputStream((commandName + " abcd" + newLine).getBytes()), printStream, printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(state, 0)}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + commandName + ": too many arguments" + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeNotEnoughArguments() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream((commandName + newLine).getBytes()),
                printStream, printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(state, 1)}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + commandName + ": not enough arguments" + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeLaunchCommandWithMistake() throws IOException {
        InterpreterState state = new TestInterpreterState(new ByteArrayInputStream((command2Name + newLine).getBytes()),
                printStream, printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{new TestTableCommandWithException(state, 0)}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT + "exception" + newLine + PROMPT, outputStream.toString());
    }

    @Test
    public void interpreterInteractiveModeExit() throws IOException { // Check that "commandName" is not executed
        InterpreterState state = new TestInterpreterState(
                new ByteArrayInputStream(("exit" + newLine + commandName + newLine).getBytes()), printStream,
                printStream, true);
        Interpreter interpreter = new Interpreter(new Command[]{new TestCommand(state, 0)}, state);

        interpreter.run(new String[]{});
        assertEquals(PROMPT, outputStream.toString());
    }
}
