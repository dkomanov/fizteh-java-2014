/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.interpeter;

import ru.fizteh.fivt.students.kalandarovshakarim.interpeter.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.AbstractCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.Command;

/**
 *
 * @author shakarim
 */
public class InterpreterTest {

    private InputStream in;
    private PrintStream out;
    private PrintStream err;

    private final OutputStream baosOut = new ByteArrayOutputStream();
    private final OutputStream baosErr = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        out = new PrintStream(baosOut);
        err = new PrintStream(baosErr);

        Command fake = new AbstractCommand("fake", 0, null, in, out, err) {

            @Override
            public void exec(String[] args) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    public Command newCommand(String name, int argsNum, InputStream input) {
        return new AbstractCommand("test", argsNum, null, input, out, err) {

            @Override
            public void exec(String[] args) throws IOException {
                super.out.println("true");
            }
        };
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of exec method, of class Interpreter.
     */
    @Test
    public void testInteractive() {
        in = new ByteArrayInputStream("test".getBytes());
        Command test = newCommand("test", 0, in);

        new Interpreter(new Command[]{test}, in, out, err).exec();
        assertEquals("$ true\n$ \n",baosOut.toString());
    }

    /**
     * Test of exec method, of class Interpreter.
     */
    @Test
    public void testBatchSatus0() {
        in = new ByteArrayInputStream("".getBytes());
        Command test = newCommand("test", 0, in);

        Interpreter interpreter = new Interpreter(new Command[]{test}, in, out, err);
        int statusOk = interpreter.exec("test");
        assertEquals("true\n",baosOut.toString());
        assertEquals(0, statusOk);
    }

    /**
     * Test of exec method, of class Interpreter.
     */
    @Test
    public void testBatchSatus1() {
        in = new ByteArrayInputStream("".getBytes());
        Command test = newCommand("test", 0, in);

        Interpreter interpreter = new Interpreter(new Command[]{test}, in, out, err);
        int statusfail = interpreter.exec("noncommand");
        assertEquals(1, statusfail);
    }

    /**
     * Test of exec method, of class Interpreter.
     */
    @Test
    public void testProcessCommandInvalidArgsNum() {
        in = new ByteArrayInputStream("".getBytes());
        Command test = newCommand("test", 0, in);

        Interpreter interpreter = new Interpreter(new Command[]{test}, in, out, err);
        int statusfail = interpreter.exec("test", "");
        assertEquals(1, statusfail);
    }

}
