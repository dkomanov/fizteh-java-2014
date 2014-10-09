package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

class Data {
    public static void load(File fl) throws Exception {
        FileInputStream input = null;
        try {
            input = new FileInputStream(fl);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found");
        }

        FileChannel inChannel = input.getChannel();
        ByteBuffer.allocate(8);
        input.close();
    }
}
