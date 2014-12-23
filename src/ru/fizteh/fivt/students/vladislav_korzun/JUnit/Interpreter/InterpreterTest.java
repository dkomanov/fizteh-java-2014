package ru.fizteh.fivt.students.vladislav_korzun.JUnit.Interpreter;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;



//import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InterpreterTest {

    private PrintStream printStream;
    private ByteArrayOutputStream outputStream;
    @Before
    public void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test 
    public void normalConstructorTest() throws Exception {
        new Interpreter(null, new Command[] {} , System.in, printStream); 
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void badlConstructorTest() throws Exception {
       new Interpreter(null, new Command[] {} , null, null); 
    }
    
    @Test 
    public void batchModeTest() throws Exception {
        Interpreter inter = new Interpreter(null, new Command[] {
                new Command("test", 0 , new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        printStream.println("test");
                    }
                })
        } , System.in, printStream);
        String[] args = new String[] { 
                new String("test")
        };
        inter.run(args);
        assertEquals("test" + System.getProperty("line.separator"), outputStream.toString());
    }

    @Test
    public void interactiveModeTest() throws Exception {
        Interpreter interpreter = new Interpreter(null,
                new Command[] {new Command("test", 0,new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        printStream.println("test");
                    }
                })},
                new ByteArrayInputStream(("test").getBytes()), printStream);
        interpreter.run(new String[]{});
        assertEquals("test", outputStream.toString());
    }
   
    
    @Test (expected = Exception.class)
    public void badArgumentsinInteractiveModTest() throws Exception {
        Interpreter inter = new Interpreter(null, new Command[] {
                new Command("test", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        printStream.println("test");
                    }
                }),
                new Command("exit", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        System.exit(0);
                    }
                })
                
        } , System.in, printStream); 
        inter.run(new String[]{});
    }
}
