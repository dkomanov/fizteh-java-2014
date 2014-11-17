package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import org.junit.Test;
import sun.reflect.annotation.ExceptionProxy;

import static org.junit.Assert.*;

public class ErrorFunctionsTest {

    final static String name = "name";
    final static String string = "string";

    @Test(expected = Exception.class)
    public void testErrorFunctionsClass() throws Exception {
        ErrorFunctions error = new ErrorFunctions();
        error.errorWrite(name); }

    @Test(expected = Exception.class)
    public void testErrorRead() throws Exception {
        ErrorFunctions.errorRead(name); }

    @Test(expected = Exception.class)
    public void testErrorWrite() throws Exception {
        ErrorFunctions.errorWrite(name); }

    @Test(expected = Exception.class)
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

    @Test(expected = Exception.class)
    public void testSecurity() throws Exception {
        ErrorFunctions.security(name, string); }

    @Test(expected = Exception.class)
    public void testSmthWrong1() throws Exception {
        ErrorFunctions.smthWrong(name); }

    @Test(expected = Exception.class)
    public void testSmthWrong2() throws Exception {
        ErrorFunctions.smthWrong(name, string); }

    @Test(expected = IllegalArgumentException.class)
    public void testTableNameIsFile() throws IllegalArgumentException {
        ErrorFunctions.tableNameIsFile(name, string); }

    @Test(expected = Exception.class)
    public void testWrongInput() throws Exception {
        ErrorFunctions.wrongInput(name); }

    @Test(expected = Exception.class)
    public void testWrongQuantity() throws Exception {
        ErrorFunctions.wrongQuantity(name); }
}