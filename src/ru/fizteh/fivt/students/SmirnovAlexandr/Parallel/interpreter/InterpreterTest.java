package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.interpreter;

import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.interpreter.exceptions.WrongCommandException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

class GreetingCommandForTest implements InterpreterCommand {
    @Override
    public void exec(final PrintStream out, final PrintStream err) throws TerminateInterpeterException {
        out.println("hello world!");
    }

    @Override
    public String getName() {
        return "greetings";
    }
}

class ParserForTest implements InterpreterCommandsParser {
    @Override
    public InterpreterCommand parseOneCommand(final Scanner input) {
        String cmdName = input.nextLine();
        switch (cmdName) {
            case "greetings":
                return new GreetingCommandForTest();
            default:
                throw new WrongCommandException(cmdName);
        }
    }

    @Override
    public List<InterpreterCommand> parseAllInput(final Scanner input) {
        List<InterpreterCommand> res = new LinkedList<>();
        res.add(parseOneCommand(input));
        return res;
    }
}

public class InterpreterTest {
    private Interpreter shell = new Interpreter(new ParserForTest());
    private InputStream input;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Test
    public void testInterpreterWithHelloWorldCommand() throws Exception {
        input = new ByteArrayInputStream("greetings".getBytes());
        shell.run(input, new PrintStream(baos), System.err, Interpreter.ExecutionMode.BATCH);
        assertEquals("hello world!" + System.lineSeparator(), baos.toString());
    }

    @After
    public void tearDown() throws Exception {
        input.close();
        baos.close();
    }
}
