package ru.fizteh.fivt.students.Volodin_Denis.JUnit.Tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.database.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FileUtilsTest {

    public final String end = ".." + File.separator + "test123321123321";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testConstructor() {
        FileUtils wwf = new FileUtils();
        wwf.get("Test");
    }

    @Test
    public void testCreateDirectoryWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        FileUtils.createDirectory(path.toString());
        if (!path.toFile().exists()) {
            throw new Exception("not exists");
        }
        Files.delete(path); }

    @Test
    public void testCreateDirectoryWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString()).toAbsolutePath();
        FileUtils.createDirectory(path.toString(), end);
        assertTrue("not exists", Paths.get(path.toString(), end).toFile().exists());
        Files.delete(Paths.get(path.toString(), end)); }

    @Test
    public void testDelete() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        Files.createDirectory(path);
        FileUtils.delete(path.toString(), "dgdfg/.."); }

    @Test
    public void testExistsWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        assertEquals(path.toFile().exists(), FileUtils.exists(path.toString())); }

    @Test
    public void testExistsWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath();
        assertEquals(path.toFile().exists(), FileUtils.exists(temporaryFolder.toString(), path.getFileName()
                .toString())); }

    @Test
    public void testGetWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path, FileUtils.get(path.toString())); }

    @Test
    public void testGetWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(Paths.get(path.toString(), "1"), FileUtils.get(path.toString(), "1")); }

    @Test
    public void testGetFileNameWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getFileName().toString(), FileUtils.getFileName(path.toString())); }

    @Test
    public void testGetFileNameWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getFileName().toString(), FileUtils
                .getFileName(path.toString(), "1" + File.separator + "..")); }


    @Test
    public void testGetParentName() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getParent().getFileName().toString(), FileUtils.getParentName(path.toString())); }

    @Test
    public void testGetParentNameWithTwoArguments() {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.getParent().getFileName().toString(),
                FileUtils.getParentName(path.toString(), "1" + File.separator + "..")); }

    @Test
    public void testIsDirectoryWithOneArgument() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.toFile().isDirectory(), FileUtils.isDirectory(path.toString())); }

    @Test
    public void testIsDirectoryWithTwoArguments() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(Paths.get(path.toString(), "../1").toFile().isDirectory(),
                FileUtils.isDirectory(path.toString(), ".." + File.separator + "/1")); }

    @Test
    public void testToAbsolutePath()  throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        assertEquals(path.toString(), FileUtils.toAbsolutePath(path.toString())); }
}
