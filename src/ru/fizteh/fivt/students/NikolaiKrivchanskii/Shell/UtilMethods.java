package ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class UtilMethods {
    public static final String ENCODING = "UTF-8";
    public static String uniteItems(Collection<?> items, String separator) {
        boolean isFirstIteration = true;
        StringBuilder joinBuilder = new StringBuilder();
        for (Object item: items) {
            if (isFirstIteration) {
                isFirstIteration = false;
            } else {
                joinBuilder.append(separator);
            }
            joinBuilder.append(item.toString());
        }
        return joinBuilder.toString();
    }
    
    public static void closeCalm(Closeable something) {
        try {
            if (something != null) {
                something.close();
            }
        } catch (IOException e) {
            System.err.println("Error aquired while trying to close " + e.getMessage());
        }
    }
    
    public static void copyFile(File source, File dirDestination) throws SomethingIsWrongException {
        File copy = new File(dirDestination, source.getName());
        FileInputStream ofOriginal = null;
        FileOutputStream ofCopy = null;
        try {
            copy.createNewFile();
            ofOriginal = new FileInputStream(source);
            ofCopy = new FileOutputStream(copy);
            byte[] buf = new byte[4096]; //size of reading = 4kB
            int read = ofOriginal.read(buf);
            while (read > 0) {
                ofCopy.write(buf, 0, read);
                read = ofOriginal.read(buf);
            }
        } catch (FileNotFoundException e) {
            throw new SomethingIsWrongException("This file or directory doesn't exist yet. " + e.getMessage());
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error while writing/reading file. " + e.getMessage());
        } finally {
            closeCalm(ofOriginal);
            closeCalm(ofCopy);
        }
    }
    
    public static File getAbsoluteName(String fileName, ShellState state) {
        File file = new File(fileName);
        
        if (!file.isAbsolute()) {
            file = new File(state.getCurDir(), fileName);
        }
        return file;
    }
    public static byte[] getBytes(String string, String encoding) throws SomethingIsWrongException {
        byte[] bytes = null;
        try {
            bytes = string.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new SomethingIsWrongException("Unable to convert string to bytes of this type: " + e.getMessage());
        }
        return bytes;
    }
    
    public static byte[] bytesToArray(ByteArrayOutputStream bytes) {
        byte[] result = new byte[bytes.size()];
        result = bytes.toByteArray();
        return result;
    }
    
    public static boolean doesExist(String path) {
        File file = new File(path);
        return file.exists();
    }
    
    public static int countBytes(String string, String encoding) {
        try {
            byte[] bytes = string.getBytes(encoding);
            return bytes.length;
        } catch (Exception e) {
            return 0;
        }
    }
}
