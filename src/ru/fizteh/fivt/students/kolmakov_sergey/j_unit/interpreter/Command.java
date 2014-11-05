package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.interpreter;

import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.StopInterpreterException;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure.DataBaseState;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.WrongNumberOfArgumentsException;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int minArguments;
    private int maxArguments;
    private BiConsumer<DataBaseState, String[]> callback;

    public Command(String name, int minArguments, int maxArguments, BiConsumer<DataBaseState, String[]> callback) {
        this.name = name;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void execute(DataBaseState dbState, String[] params) throws WrongNumberOfArgumentsException,
            StopInterpreterException {
        if (!(minArguments <= params.length && params.length <= maxArguments)) {
            throw new WrongNumberOfArgumentsException(name + ": incorrect number of arguments");
        } else {
            callback.accept(dbState, params);
        }
    }
}