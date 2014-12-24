package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.interpreter;

import java.util.List;
import java.util.Scanner;

public interface InterpreterCommandsParser {
    /**
     * Method reads from input and parse one command.
     *
     * @param input Source for parsing.
     * @return Parsed command.
     */
    InterpreterCommand parseOneCommand(final Scanner input);

    /**
     * Method parses all input for commands.
     *
     * @param input Source for parsing.
     * @return List of parsed commands.
     */
    List<InterpreterCommand> parseAllInput(final Scanner input);


}
