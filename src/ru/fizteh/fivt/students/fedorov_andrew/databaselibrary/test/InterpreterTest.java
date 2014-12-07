package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.AlternativeShellState;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class InterpreterTest extends InterpreterTestBase<AlternativeShellState> {
    private AlternativeShellState state;

    @Override
    protected Shell<AlternativeShellState> constructInterpreter() throws TerminalException {
        state = new AlternativeShellState();
        return new Shell<>(state);
    }

    @Test
    public void testUnexpectedMethodError() throws TerminalException {
        runBatchExpectNonZero(AlternativeShellState.THROW_RUNTIME.getName());

        assertThat("Improper error report", getOutput(), containsString("Method execution error"));
    }

    @Test
    public void testInteractiveContinueWorkAfterError() throws TerminalException {
        String echoMessage = "hello, man";

        runInteractiveExpectZero(
                AlternativeShellState.THROW_RUNTIME.getName(),
                AlternativeShellState.ECHO.getName() + ' ' + echoMessage);

        String regex = makeTerminalExpectedRegex(
                GREETING_REGEX, "(.* )?Method execution error", echoMessage);

        assertTrue("Interactive: should be okay after error", getOutput().matches(regex));
    }

    @Test
    public void testErrorInPersistOnExit() throws TerminalException {
        state.setMakeExceptionOnPersist(true);

        runBatchExpectNonZero();

        assertEquals("Expecting empty output", getOutput(), makeTerminalExpectedMessage());
    }

    @Test
    public void testErrorOnInit() {
        state.setMakeExceptionOnInit(true);

        try {
            interpreter = new Shell<>(state);
        } catch (TerminalException exc) {
            assertThat(
                    "Improper error message", exc.getMessage(), containsString("Spontanious exception"));
        }

        assertEquals(
                "Improper error output", getOutput(), makeTerminalExpectedMessage("Spontanious exception"));
    }
}
