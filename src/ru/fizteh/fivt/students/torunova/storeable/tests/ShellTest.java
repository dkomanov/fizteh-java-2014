package ru.fizteh.fivt.students.torunova.storeable.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.torunova.storeable.database.actions.Action;
import ru.fizteh.fivt.students.torunova.storeable.interpreter.Shell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ShellTest {
    Shell shell;
    ByteArrayOutputStream out;
    Set<Action> actions;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Before
    public void setUp() throws Exception {
        out = new ByteArrayOutputStream();
        actions = new HashSet<>();
        actions.add(new StupidAction(out));
        actions.add(new StupidExitAction(out));
        actions.add(new StupidActionWithException());
    }
    @Test
    public void testRunWithExistingCommand() {
        shell = new Shell(actions, new ByteArrayInputStream("stupid some arguments;exit".getBytes()), out, "exit", false);
        shell.run();
        String buffer = out.toString();
        assertEquals("stupid some arguments", buffer.substring(0, buffer.indexOf("\n")));
    }
    @Test
    public void testRunWithNonExistentCommand() {
        shell = new Shell(actions, new ByteArrayInputStream("strangeCommand some arguments;exit".getBytes()), out, "exit", false);
        shell.run();
        String buffer = out.toString();
        assertEquals("Command not found.", buffer.substring(0, buffer.indexOf("\n")));
    }
    @Test
    public void testRunCommandWithException() {
        shell = new Shell(actions, new ByteArrayInputStream("stupidExcept some arguments;exit".getBytes()), out, "exit", false);
        shell.run();
        String buffer = out.toString();
        assertEquals("Caught StupidException: exception", buffer.substring(0, buffer.indexOf("\n")));
    }


}