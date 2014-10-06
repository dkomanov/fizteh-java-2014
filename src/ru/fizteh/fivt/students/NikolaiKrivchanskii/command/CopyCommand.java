package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.SomethingIsWrongException;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.UtilMethods;


public class CopyCommand implements Commands<ShellState> {

    public String getCommandName() {
        return "cp";
    }
    
    public int getArgumentQuantity() {
        return 2;
    }
    
    
    public void implement(String[] args, ShellState state) throws SomethingIsWrongException {
        if (!args[0].equals("-r") && args.length == 2) {
            String from = args[0];
            String to = args[1];
            File source = UtilMethods.getAbsoluteName(from, state);
            if (source.isDirectory() && source.list().length != 0) {
                throw new SomethingIsWrongException("To copy directory, which is not empty use \"-r\" flag.");
            }
            File newPlace = UtilMethods.getAbsoluteName(to, state);
            if (!newPlace.isDirectory()) {
                if (!newPlace.exists()) {
                    copyToNotExistingFile(source, newPlace);
                    return;
                } else {
                    throw new SomethingIsWrongException("This file already exists. ");
                }
            }
            mkCopy(source, newPlace);
        } else if (args[0].equals("-r") && args.length == 3) {
            String from = args[1];
            String to = args[2];
            File source = UtilMethods.getAbsoluteName(from, state);
            File newPlace = UtilMethods.getAbsoluteName(to, state);
            if (!source.isDirectory()) {
                copy(source, newPlace);
                return;
            }
            File newDir = new File(newPlace, from);
            if (!newDir.getAbsoluteFile().mkdirs()) {
                throw new SomethingIsWrongException("error creating directory");
            }
            recursiveCopy(source, newDir);
        } else {
            throw new SomethingIsWrongException("wrong flag. note that only \"-r\" flag is supported");
        }
    }
    
    
    private void recursiveCopy(File startPoint, File destination) throws SomethingIsWrongException {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File tempDestination = new File(destination.getAbsolutePath(), file.getName());
                    try {
                        Files.copy(file.toPath(), tempDestination.toPath());
                    } catch (IOException e) {
                        throw new SomethingIsWrongException(e.getMessage());
                    }
                } else {
                    File newDir = new File(destination, file.getName()).toPath().normalize().toFile();
                    newDir = newDir.toPath().normalize().toFile();
                    try {
                        if (!newDir.getCanonicalFile().mkdirs()) {
                            throw new SomethingIsWrongException("error occured while creating directory");
                        }
                    } catch (IOException e) {
                        throw new SomethingIsWrongException(e.getMessage());
                    }
                    recursiveCopy(file, newDir);
                }
            }
        }
    }
    
    private static void copyToNotExistingFile(File from, File to) throws SomethingIsWrongException {
        File copy = new File(to, to.getName());
        FileInputStream first = null; //file from copy was made
        FileOutputStream second = null; // file to
        try {
            first = new FileInputStream(from);
            second = new FileOutputStream(to);
            byte[]buf = new byte[4096];
            int read = first.read(buf);
            while (0 < read) {
                second.write(buf, 0, read);
                read = first.read(buf);
            }
        } catch (FileNotFoundException e) {
            throw new SomethingIsWrongException("File not found. " + e.getMessage());
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error acquired while reading/writing a file. " + e.getMessage());
        } finally {
            UtilMethods.closeCalm(first);
            UtilMethods.closeCalm(second);
        }
    }
    
    protected static void copy(File from, File to) throws SomethingIsWrongException {
        File copy = new File(to, from.getName());
        FileInputStream first = null; //file from copy was made
        FileOutputStream second = null; // file to
        try {
            copy.createNewFile();
            first = new FileInputStream(from);
            second = new FileOutputStream(copy);
            byte[]buf = new byte[4096];
            int read = first.read(buf);
            while (0 < read) {
                second.write(buf, 0, read);
                read = first.read(buf);
            }
        } catch (FileNotFoundException e) {
            throw new SomethingIsWrongException("File not found. " + e.getMessage());
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error acquired while reading/writing a file. " + e.getMessage());
        } finally {
            UtilMethods.closeCalm(first);
            UtilMethods.closeCalm(second);
        }
    }
    
    private void mkCopy(File from, File to) throws SomethingIsWrongException {
        if (from.isFile()) {
            copy(from, to);
            return;
        }
        File newPlace = new File(to, from.getName());
        if (!newPlace.exists() || !newPlace.mkdir()) {
            throw new SomethingIsWrongException("Unable to create a new directory " + from.getName());
        }
        for (String temp : from.list()) {
            copy(new File(to, temp), newPlace); 
        }
    }
    

}
