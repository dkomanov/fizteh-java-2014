package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.interpreter;

import java.util.List;
import java.util.Scanner;

public interface InterpreterCommandsParser {

    InterpreterCommand parseOneCommand(final Scanner input);

    List<InterpreterCommand> parseAllInput(final Scanner input);


}
