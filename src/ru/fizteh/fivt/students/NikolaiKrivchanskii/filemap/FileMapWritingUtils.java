package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Set;



public class FileMapWritingUtils implements Closeable {
    
    protected static RandomAccessFile dataFile;
    public FileMapWritingUtils(String filePath) throws IOException {
        try {
                dataFile = new RandomAccessFile(filePath, "rw");
            }
            catch (FileNotFoundException e) {
                throw new IOException(String.format("error while creating file: '%s'", filePath));
            }
            try {
                dataFile.setLength(0);
            } catch (IOException e) {
                throw new IOException("Error aqcuired while resizing a file " + e.getMessage());
            }
    }
    
    public void writeKey(String key) throws IOException {
        byte[] bytes = GlobalUtils.getBytes(key, GlobalUtils.ENCODING);
        dataFile.write(bytes);
        dataFile.writeByte(0);
    }
    
    public static void writeOnDisk(Set<String> keys, String file, TableBuilder builder) throws IOException {
        FileMapWritingUtils write = new FileMapWritingUtils(file);
        int shift = GlobalUtils.getKeysLength(keys, GlobalUtils.ENCODING);
        for (String key : keys) {
        	write.writeKey(key);
        	write.writeOffset(shift);
        	shift += GlobalUtils.countBytes(builder.get(key), GlobalUtils.ENCODING);
        }
        for (String key : keys) {
        	write.writeValue(builder.get(key));
        }
        write.close();
    }

    public void writeOffset(int offset) throws IOException {
            dataFile.writeInt(offset);
    }

    public void writeValue(String value) throws IOException {
        try {
            byte[] bytes = value.getBytes(GlobalUtils.ENCODING);
            dataFile.write(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unrecognized encoding");
        }
    }
    
    public void close() {
        GlobalUtils.closeCalm(dataFile);
    }
    
}