package ru.fizteh.fivt.students.Volodin_Denis.JUnit.main;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions.*;

public class ErrorFunctions {

    public static void errorRead() throws DatabaseReadErrorException {
        throw new DatabaseReadErrorException("error reading from file");
    }

    public static void errorWrite() throws DatabaseWriteErrorException {
        throw new DatabaseWriteErrorException("error writing to file");
    }
    
    public static void invalidName(final String commandName, final String arg)
            throws InterpreterInvalidCommandNameException {
        throw new InterpreterInvalidCommandNameException(commandName + ": [" + arg + "] is invalid name.");
    }

    public static void nameIsNull(final String commandName,  final String arg) throws IllegalArgumentException {
        throw new IllegalArgumentException(commandName + ": [" + arg + "] is null.");
    }

    public static void notDirectory(final String commandName,  final String arg) throws IllegalStateException {
        throw new IllegalStateException(commandName + ": [" + arg + "] is not a directory.");
    }

    public static void notExists(final String commandName, final String arg) throws IllegalStateException {
        throw new IllegalStateException(commandName + ": [" + arg + "] does not exists.");
    }

    public static void notMkdir(final String commandName, final String arg) throws IllegalArgumentException {
        throw new IllegalArgumentException(commandName + ": failed to create a directory [" + arg + "].");
    }

    public static void security(final String commandName, final String arg) throws ProhibitedAccessException {
        throw new ProhibitedAccessException(commandName + ": access to the [" + arg + "] is prohibited.");
    }

    public static void smthWrong(final String commandName) throws SomethingWrongException {
        throw new SomethingWrongException(commandName + "something gone wrong.");
    }

    public static void smthWrong(final String commandName, final String message) throws SomethingWrongException {
        throw new SomethingWrongException(commandName + " :" + message);
    }

    public static void tableNameIsFile(final String commandName, final String arg) throws IllegalArgumentException {
        throw new IllegalArgumentException(commandName + ": [" + arg + "] is file");
    }

    public static void wrongInput(final String commandName) throws WrongInputException {
        throw new WrongInputException(commandName + ": wrong input.");
    }

    public static void wrongQuantityOfArguments(final String commandName) throws WrongQuantityOfArgumentsException {
        throw new WrongQuantityOfArgumentsException(commandName + ": wrong quantity of arguments.");
    }
}
