package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;


public class ReadingUtils {
    
    protected RandomAccessFile tempFile;
    private static int valueShift = -1;
    
    public ReadingUtils(String pathToFile) throws SomethingIsWrongException {
        try {
            tempFile = new RandomAccessFile(pathToFile, "r");
        } catch (FileNotFoundException e) {
            tempFile = null;
            valueShift = 0;
            return;
        }
        skipKey();
        valueShift = readOffset();
        try {
            tempFile.seek(0);
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error aqcuired while seeking through file: " + e.getMessage());
        }
    }
    
    public String readKey() throws SomethingIsWrongException {
        byte[] array;
        try {
            if (tempFile.getFilePointer() >= valueShift) {
                return null;
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte b = tempFile.readByte();
            while (b != 0) {
                bytes.write(b);
                b = tempFile.readByte();
            }
            array = UtilMethods.bytesToArray(bytes);
        } catch (IOException e) {
            throw new SomethingIsWrongException("Error acquired: " + e.getMessage());
        }
        String returnKey;
        try {
            returnKey = new String(array, UtilMethods.ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new SomethingIsWrongException("Error acquired: " + e.getMessage());
        }
        return returnKey;
    }
    
        public String readValue() throws SomethingIsWrongException {
            int offset = readOffset();
            int nextOffset = readNextOffset();
            long currentOffset;
            try {
                currentOffset = tempFile.getFilePointer();
                tempFile.seek(offset);
                int valueLength = nextOffset - offset;
                byte[] bytes = new byte[valueLength];
                tempFile.read(bytes, 0, valueLength);
                tempFile.seek(currentOffset);
                return new String(bytes, UtilMethods.ENCODING);
            } catch (IOException e) {
                throw new SomethingIsWrongException("Error acquired: " + e.getMessage());
            }
            
        }
    
        public boolean endOfFile() throws SomethingIsWrongException {
            if (tempFile == null) {
                return true;
            }
    
            boolean result = true;
            try {
                result = (tempFile.getFilePointer() == valueShift);
            } catch (EOFException ee) {
                return result;
            } catch (IOException e) {
                 if (e.getMessage() != null) {
                     throw new SomethingIsWrongException("Error aqcuired while reading a file " + e.getMessage());
                 }
            }
            return result;
        }
    
    
        private int readNextOffset() throws SomethingIsWrongException {
            int nextOffset = 0;
            try {
                int currentOffset = (int) tempFile.getFilePointer();
                if (readKey() == null) {
                    nextOffset = (int) tempFile.length();
                } else {
                    nextOffset = readOffset();
                }
                tempFile.seek(currentOffset);
            } catch (IOException e) {
                 if (e.getMessage() != null) {
                      if (e.getMessage() != null) {
                        throw new SomethingIsWrongException("Error aqcuired while reading a file " + e.getMessage());
                    }
                 }
            }
            return nextOffset;
        }
    
        private void skipKey() throws SomethingIsWrongException {
            byte b = 0;
            do {
                try {
                    b = tempFile.readByte();
                } catch (IOException e) {
                     if (e.getMessage() != null) {
                        throw new SomethingIsWrongException("Error aqcuired while reading a file " + e.getMessage());
                     }
                }
            } while(b != 0);
        }
    
        private int readOffset() throws SomethingIsWrongException {
            try {
                return tempFile.readInt();
            } catch (IOException e) {
                if (e.getMessage() != null) {
                    throw new SomethingIsWrongException("Error aqcuired while reading a file " + e.getMessage());
                 } else {
                     throw new SomethingIsWrongException("Empty file");
                 }
            }
        }
}
