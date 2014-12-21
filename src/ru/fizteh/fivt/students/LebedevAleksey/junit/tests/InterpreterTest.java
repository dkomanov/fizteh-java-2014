package ru.fizteh.fivt.students.LebedevAleksey.junit.tests;

import org.junit.*;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.Interpreter;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.InterpreterState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class InterpreterTest {
    public static final String TEST_COMMAND_NAME = "test";
    private static PrintStream standartOut;
    private static PrintStream standartErr;
    private static InputStream standartIn;
    private static ByteArrayOutputStream testOutput;
    private static PrintStream testOut;
    private static ByteArrayOutputStream testError;
    private static PrintStream testErr;

    @BeforeClass
    public static void setUp() {
        standartOut = System.out;
        standartErr = System.err;
        standartIn = System.in;
        testOutput = new ByteArrayOutputStream();
        testOut = new PrintStream(testOutput);
        testError = new ByteArrayOutputStream();
        testErr = new PrintStream(testError);
        System.setOut(testOut);
        System.setErr(testErr);
    }

    @AfterClass
    public static void tearDown() {
        System.setOut(standartOut);
        System.setIn(standartIn);
        System.setErr(standartErr);
    }

    @Test
    public void testInvokeCommands() {
        Interpreter interpreter = createInterpreterWithTestCommand();
        interpreter.run(new String[]{TEST_COMMAND_NAME});
        Assert.assertEquals("TestMessage" + System.lineSeparator(), testOutput.toString());
    }

    @Test
    public void testBatchModeWithOneCommand() {
        Interpreter interpreter = createInterpreterWithTestCommand();
        interpreter.run(new String[]{TEST_COMMAND_NAME + ";", TEST_COMMAND_NAME + ";", TEST_COMMAND_NAME});
        String expected = "TestMessage" + System.lineSeparator() + "TestMessage"
                + System.lineSeparator() + "TestMessage" + System.lineSeparator();
        Assert.assertEquals(expected, testOutput.toString());
    }

    @Test
    public void testInteractiveModeWithOneCommand() {
        List<Command> commands = new ArrayList<>();
        commands.add(new Command("exit", 0) {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.println("TestMessage");
                state.exit();
                return false;
            }
        });
        Interpreter interpreter = new Interpreter(commands, new InterpreterState());
        System.setIn(new ByteArrayInputStream(("exit" + System.lineSeparator()).getBytes()));
        interpreter.run(new String[0]);
        String expected = "$ TestMessage" + System.lineSeparator();
        Assert.assertEquals(expected, testOutput.toString());
    }

    @Test
    public void testInteractiveModeWithSeveralCommands() {
        Interpreter interpreter = createInterpreterWithTestAndExitCommands();
        System.setIn(new ByteArrayInputStream((TEST_COMMAND_NAME + System.lineSeparator() + TEST_COMMAND_NAME
                + System.lineSeparator() + "exit" + System.lineSeparator()).getBytes()));
        interpreter.run(new String[0]);
        String expected = "$ TestMessage" + System.lineSeparator() + "$ TestMessage" + System.lineSeparator()
                + "$ Terminated" + System.lineSeparator();
        Assert.assertEquals(expected, testOutput.toString());
    }

    @Test
    public void testBatchModeWithSeveralCommands() {
        Interpreter interpreter = createInterpreterWithTestAndExitCommands();
        interpreter.run(new String[]{TEST_COMMAND_NAME + ";", TEST_COMMAND_NAME});
        String expected = "TestMessage" + System.lineSeparator() + "TestMessage" + System.lineSeparator();
        Assert.assertEquals(expected, testOutput.toString());
    }

    @Test
    public void testExitCommandInBatchMode() {
        Interpreter interpreter = createInterpreterWithTestAndExitCommands();
        interpreter.run(new String[]{TEST_COMMAND_NAME + ";exit;", TEST_COMMAND_NAME});
        String expected = "TestMessage" + System.lineSeparator() + "Terminated" + System.lineSeparator();
        Assert.assertEquals(expected, testOutput.toString());
    }

    @Test
    public void testCorrectArgumentsParsingBatchMode() {
        List<Command> commands = new ArrayList<>();
        commands.add(new Command("print") {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.print(arguments.length);
                for (String str : arguments) {
                    System.out.print(" " + str);
                }
                return true;
            }
        });

        compareBatch(commands, new String[]{"print\t"}, "0");
        compareBatch(commands, new String[]{"print "}, "0");
        compareBatch(commands, new String[]{"\tprint"}, "0");
        compareBatch(commands, new String[]{" print"}, "0");
        compareBatch(commands, new String[]{"print\t\t"}, "0");
        compareBatch(commands, new String[]{"print "}, "0");
        compareBatch(commands, new String[]{"\t\tprint"}, "0");
        compareBatch(commands, new String[]{"  print"}, "0");
        compareBatch(commands, new String[]{"print\t "}, "0");
        compareBatch(commands, new String[]{"print \t"}, "0");
        compareBatch(commands, new String[]{"\t print"}, "0");
        compareBatch(commands, new String[]{" \tprint"}, "0");

        compareBatch(commands, new String[]{"print", "\t; print", "123"}, "01 123");
        compareBatch(commands, new String[]{"print", "\t; print", "123", "45\t;print ;print"}, "02 123 4500");
        compareBatch(commands, new String[]{"print", "\t; print", "123", "45;\tprint; print"}, "02 123 4500");
        compareBatch(commands, new String[]{"print", "\t; print", "123", "45\t;\tprint ; print"}, "02 123 4500");
        compareBatch(commands, new String[]{"print", "\t; print", "123", "45 ;\tprint ;\tprint"}, "02 123 4500");
        compareBatch(commands, new String[]{"print", "\t; print", "123", "45\t; print\t; print"}, "02 123 4500");

        compareBatch(commands, new String[]{"\tprint"}, "0");
        compareBatch(commands, new String[]{"\t\tprint"}, "0");
        compareBatch(commands, new String[]{" \tprint"}, "0");
        compareBatch(commands, new String[]{"print "}, "0");
        compareBatch(commands, new String[]{"print\t"}, "0");
        compareBatch(commands, new String[]{"print \t"}, "0");
        compareBatch(commands, new String[]{"print\t "}, "0");
        compareBatch(commands, new String[]{"print  "}, "0");
        compareBatch(commands, new String[]{"print\t\t"}, "0");
        compareBatch(commands, new String[]{"print "}, "0");
        compareBatch(commands, new String[]{"\tprint\t"}, "0");
        compareBatch(commands, new String[]{" print \t"}, "0");
        compareBatch(commands, new String[]{"\t\tprint\t "}, "0");
        compareBatch(commands, new String[]{"print  "}, "0");
        compareBatch(commands, new String[]{"print\t\t"}, "0");

        commands.add(new Command("secondCommand") {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.println("Output");
                return true;
            }
        });
        compareBatch(commands, new String[]{"print", ";secondCommand;print"},
                "0Output" + System.lineSeparator() + "0");
    }

    @Test
    public void testCorrectArgumentsParsingInteractiveMode() {
        List<Command> commands = new ArrayList<>();
        commands.add(new Command("print") {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.print(arguments.length);
                for (String str : arguments) {
                    System.out.print(" " + str);
                }
                return true;
            }
        });
        commands.add(new Command("exit", 0) {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.println("Terminated");
                state.exit();
                return false;
            }
        });
        compareInteractive(commands, "print 345 abc \"de fg\"         \"hij\"\t \tklmn;print\t;print\t;exit",
                "$ 5 345 abc de fg hij klmn00Terminated" + System.lineSeparator());
        compareInteractive(commands, "                  print          \t\t\t\t\t  test          \t\t\t       "
                        + System.lineSeparator() + "exit", "$ 1 test$ Terminated" + System.lineSeparator()
        );
        compareInteractive(commands,
                "\t\t\t\t\t    print\t\"qwe rty\tyu\"   1 2 3  \t\t  test   j;\tprint\t;\tprint; print ;exit",
                "$ 6 qwe rty\tyu 1 2 3 test j000Terminated" + System.lineSeparator());
        compareInteractive(commands, "exit\t", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "exit ", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "\texit", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, " exit", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "exit\t\t", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "exit ", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "\t\texit", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "  exit", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "exit\t ", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "exit \t", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, "\t exit", "$ Terminated" + System.lineSeparator());
        compareInteractive(commands, " \texit", "$ Terminated" + System.lineSeparator());
    }

    private void compareInteractive(List<Command> commands, String input, String expected) {
        Interpreter interpreter;
        prepareStreams();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        interpreter = new Interpreter(commands, new InterpreterState());
        interpreter.run(new String[0]);
        Assert.assertEquals(expected, testOutput.toString());
    }

    private void compareBatch(List<Command> commands, String[] input, String expected) {
        Interpreter interpreter;
        prepareStreams();
        interpreter = new Interpreter(commands, new InterpreterState());
        interpreter.run(input);
        Assert.assertEquals(expected, testOutput.toString());
    }

    private Interpreter createInterpreterWithTestCommand() {
        List<Command> commands = new ArrayList<>();
        commands.add(new Command(TEST_COMMAND_NAME, 0) {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.println("TestMessage");
                return true;
            }
        });
        return new Interpreter(commands, new InterpreterState());
    }

    private Interpreter createInterpreterWithTestAndExitCommands() {
        List<Command> commands = new ArrayList<>();
        commands.add(new Command(TEST_COMMAND_NAME, 0) {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.println("TestMessage");
                return true;
            }
        });
        commands.add(new Command("exit", 0) {
            @Override
            protected boolean action(InterpreterState state, String[] arguments) {
                System.out.println("Terminated");
                state.exit();
                return false;
            }
        });
        return new Interpreter(commands, new InterpreterState());
    }

    @Before
    public void prepareStreams() {
        testOutput.reset();
        testError.reset();
    }
}
