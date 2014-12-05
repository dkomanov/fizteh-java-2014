package ru.fizteh.fivt.students.egor_belikov.JUnit.UnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.egor_belikov.JUnit.JUnit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

public class UnitTestsForCommands {
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        //outputStream = new ByteArrayOutputStream();
        JUnit.isInteractiveMode = true;
    }

    @Test
    public final void executeWithInvalidCommand() throws Exception {
        try {
            JUnit.execute("wrong");
        } catch (Exception e) {
            assertEquals("Invalid command", e.getMessage());
        }
    }

    @Test
    public final void executeWithoutTable() throws Exception {
        try {
            JUnit.execute("put t t");
        } catch (Exception e) {
            assertEquals("no table", e.getMessage());
        }
        //assertEquals("no table\n", outputStream.toString());
    }

    @Test
    public final void executeWithWrongArguments() throws Exception {
        try {
            JUnit.execute("exit 1");
        } catch (Exception e) {
            assertEquals("exit: invalid number of arguments", e.getMessage());
        }
    }

    @Test
    public final void executeWithNoString() throws Exception {
        JUnit.execute("");
        assertEquals("", outputStream.toString());
    }

    @Test
    public final void executeWithNull() throws Exception {
        JUnit.execute(null);
        assertEquals("", outputStream.toString());
    }

    @Test
    public final void showTablesWithWrongArguments() throws Exception {
        try {
            JUnit.execute("show tables 1");
        } catch (Exception e) {
            assertEquals("show: invalid number of arguments", e.getMessage());
        }
    }

    @Test
    public final void showTablesWithWrongArguments1() throws Exception {
        try {
            JUnit.execute("show table");
        } catch (Exception e) {
            assertEquals("Invalid args", e.getMessage());
        }
    }

    @After
    public void tearDown() throws IOException {
        outputStream.close();
    }
}
