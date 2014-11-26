package ru.fizteh.fivt.students.alina_chupakhina.junit.test;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.alina_chupakhina.junit.Interpreter;
import ru.fizteh.fivt.students.alina_chupakhina.junit.Mode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ModeTest {
    private final String newLine = System.getProperty("line.separator");
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private String testCommand = "testCommand";

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test
    public final void testBatchModeWithInvalidCommandAtTheInvalidCommand() throws Exception {
        Mode mode = new Mode(new ByteArrayInputStream(new byte[] {}), printStream);
        try {
            mode.batch(new String[]{testCommand});
        } catch (Exception e) {
            TestCase.assertEquals(testCommand + Interpreter.MESSAGE_INVALID_COMMAND, e.getMessage());
        }
    }

    @Test
    public final void testBatchModeWithNullString() throws Exception {
        Mode mode = new Mode(new ByteArrayInputStream(new byte[] {}), printStream);
        mode.batch(new String[]{});
    }

    @Test
    public final void testBatchModeWithIllegalNumberOfElements() throws Exception {
        Mode mode = new Mode(new ByteArrayInputStream(new byte[] {}), printStream);

        try {
            mode.batch(new String[]{"use"});
        } catch (Exception e) {
            assertEquals("use" + Interpreter.MESSAGE_INVALID_NUMBER_OF_ARGUMENTS, e.getMessage());
        }
    }

    @Test
    public final void testInteractiveModeWithInvalidCommandAtTheInvalidCommand() throws Exception {
        Mode mode = new Mode(new ByteArrayInputStream(testCommand.getBytes()), printStream);
        try {
            mode.interactive();
        } catch (NullPointerException e) {
            assertEquals(Mode.WELLCOME + testCommand + Interpreter.MESSAGE_INVALID_COMMAND
                    + newLine + Mode.WELLCOME, outputStream.toString());
        }
    }


    @Test
    public final void testInteractiveModeWithIllegalNumberOfElements() throws Exception {
        Mode mode = new Mode(new ByteArrayInputStream(new String("use").getBytes()), printStream);
        try {
            mode.interactive();
        } catch (NullPointerException e) {
            assertEquals(Mode.WELLCOME + "use" + Interpreter.MESSAGE_INVALID_NUMBER_OF_ARGUMENTS
                    + newLine + Mode.WELLCOME, outputStream.toString());
        }
    }

    @Test
    public final void testInteractiveModeWithEmptyString() throws Exception {
        Mode mode = new Mode(new ByteArrayInputStream(new String(newLine).getBytes()), printStream);
        try {
            mode.interactive();
        } catch (NullPointerException e) {
            assertEquals(Mode.WELLCOME + Mode.WELLCOME, outputStream.toString());
        }
    }

    @After
    public void tearDown() throws IOException {
        outputStream.close();
        printStream.close();
    }
}
