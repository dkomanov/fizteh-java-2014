package ru.fizteh.fivt.students.Volodin_Denis.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions.*;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.ErrorFunctions;

public class ErrorFunctionsTest {

    final String name = "name";
    final String string = "string";

    //@SuppressWarnings("static-access")
    @Test(expected = Exception.class)
    public void testErrorFunctionsClass() throws Exception {
        ErrorFunctions error = new ErrorFunctions();
        error.nameIsNull(name, string); }

    @Test(expected = DatabaseReadErrorException.class)
    public void testErrorRead() throws Exception {
        ErrorFunctions.errorRead(); }

    @Test(expected = DatabaseWriteErrorException.class)
    public void testErrorWrite() throws Exception {
        ErrorFunctions.errorWrite(); }

    @Test(expected = InterpreterInvalidCommandNameException.class)
    public void testInvalidName() throws Exception {
        ErrorFunctions.invalidName(name, string); }

    @Test(expected = IllegalArgumentException.class)
    public void testNameIsNull() throws IllegalArgumentException {
        ErrorFunctions.nameIsNull(name, string); }

    @Test(expected = IllegalStateException.class)
    public void testNotDirectory() throws IllegalStateException {
        ErrorFunctions.notDirectory(name, string); }

    @Test(expected = IllegalStateException.class)
    public void testNotExists() throws IllegalStateException {
        ErrorFunctions.notExists(name, string); }

    @Test(expected = IllegalArgumentException.class)
    public void testNotMkdir() throws IllegalArgumentException {
        ErrorFunctions.notMkdir(name, string); }

    @Test(expected = ProhibitedAccessException.class)
    public void testSecurity() throws Exception {
        ErrorFunctions.security(name, string); }

    @Test(expected = SomethingWrongException.class)
    public void testSmthWrong1() throws Exception {
        ErrorFunctions.smthWrong(name); }

    @Test(expected = SomethingWrongException.class)
    public void testSmthWrong2() throws Exception {
        ErrorFunctions.smthWrong(name, string); }

    @Test(expected = IllegalArgumentException.class)
    public void testTableNameIsFile() throws IllegalArgumentException {
        ErrorFunctions.tableNameIsFile(name, string); }

    @Test(expected = WrongInputException.class)
    public void testWrongInput() throws Exception {
        ErrorFunctions.wrongInput(name); }

    @Test(expected = WrongQuantityOfArgumentsException.class)
    public void testWrongQuantity() throws Exception {
        ErrorFunctions.wrongQuantityOfArguments(name); }
}
