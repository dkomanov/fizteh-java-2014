package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;


import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;



public class WritingUtils {
    
    protected RandomAccessFile dataFile;
    public WritingUtils(String filePath) throws SomethingIsWrongException {
        try {
                dataFile = new RandomAccessFile(filePath, "rw");
            } catch (FileNotFoundException e) {
                throw new SomethingIsWrongException(String.format("error while creating file: '%s'", filePath));
            }
            try {
                dataFile.setLength(0);
            } catch (IOException e) {
                throw new SomethingIsWrongException("Error aqcuired while resizing a file " + e.getMessage());
            }
    }
    
    public void writeKey(String key) throws SomethingIsWrongException {
        byte[] bytes = UtilMethods.getBytes(key, UtilMethods.ENCODING);
        try {
            dataFile.write(bytes);
            dataFile.writeByte(0);
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error acquired while writing to file: " + e.getMessage());
        }
    }

    public void writeOffset(int offset) throws SomethingIsWrongException {
        try {
            dataFile.writeInt(offset);
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error acquired while writing to file: " + e.getMessage());
        }
    }

    public void writeValue(String value) throws SomethingIsWrongException {
        try {
            byte[] bytes = value.getBytes(UtilMethods.ENCODING);
            dataFile.write(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unrecognized encoding");
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error acquired while writing to file: " + e.getMessage());
        } 
    }
    
}
