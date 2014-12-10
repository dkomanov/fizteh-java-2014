package ru.fizteh.fivt.students.Volodin_Denis.JUnit.Tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.database.WorkWithFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class WorkWithFileTest {

    public final String end = ".." + File.separator + "test123321123321";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testConstructor() {
        WorkWithFile wwf = new WorkWithFile();
        wwf.get("Test");
    }

    @Test
    public void testCreateDirectoryWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        WorkWithFile.createDirectory(path.toString());
        if (!path.toFile().exists()) {
            throw new Exception("not exists");
        }
        Files.delete(path); }

    @Test
    public void testCreateDirectoryWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString()).toAbsolutePath();
        WorkWithFile.createDirectory(path.toString(), end);
        if (!Paths.get(path.toString(), end).toFile().exists()) {
            throw new Exception("not exists");
        }
        Files.delete(Paths.get(path.toString(), end)); }

    @Test
    public void testDelete() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        Files.createDirectory(path);
        WorkWithFile.delete(path.toString(), "dgdfg/.."); }

    @Test
    public void testExistsWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        assertEquals(path.toFile().exists(), WorkWithFile.exists(path.toString())); }

    @Test
    public void testExistsWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        assertEquals(path.toFile().exists(), WorkWithFile.exists(temporaryFolder.toString(), path.getFileName()
                .toString())); }

    @Test
    public void testGetWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path, WorkWithFile.get(path.toString())); }

    @Test
    public void testGetWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(Paths.get(path.toString(), "1"), WorkWithFile.get(path.toString(), "1")); }

    @Test
    public void testGetFileNameWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getFileName().toString(), WorkWithFile.getFileName(path.toString())); }

    @Test
    public void testGetFileNameWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getFileName().toString(), WorkWithFile
                .getFileName(path.toString(), "1" + File.separator + "..")); }


    @Test
    public void testGetParentName() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getParent().getFileName().toString(), WorkWithFile.getParentName(path.toString())); }

    @Test
    public void testGetParentNameWithTwoArguments() {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getParent().getFileName().toString(),
                WorkWithFile.getParentName(path.toString(), "1" + File.separator + "..")); }

    @Test
    public void testIsDirectoryWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.toFile().isDirectory(), WorkWithFile.isDirectory(path.toString())); }

    @Test
    public void testIsDirectoryWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(Paths.get(path.toString(), "../1").toFile().isDirectory(),
                WorkWithFile.isDirectory(path.toString(), ".." + File.separator + "/1")); }

    @Test
    public void testToAbsolutePath()  throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.toString(), WorkWithFile.toAbsolutePath(path.toString())); }
}
