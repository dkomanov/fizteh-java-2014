package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Utils {

    private static final long MAX_SIZE = 1024 * 1024;
    private static final int POS_STEP = 5;
    private static final int BUF_SIZE = 4096;

    private static String readKey(final DataInputStream dataStream) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        byte b = dataStream.readByte();
        int length = 0;
        while (b != 0) {
            byteOutputStream.write(b);
            b = dataStream.readByte();
            length++;
            if (length > MAX_SIZE) {
                throw new IOException("wrong data format");
            }
        }
        if (length == 0) {
            throw new IOException("wrong data format");
        }

        return byteOutputStream.toString(StandardCharsets.UTF_8.toString());
    }

    private static String readValue(final DataInputStream dis, final long offset1,
            final long offset2, final long position, final long len) throws IOException {
        dis.mark((int) len);
        dis.skip(offset1 - position);
        byte[] buffer = new byte[(int) (offset2 - offset1)];
        dis.read(buffer);
        String value = new String(buffer, StandardCharsets.UTF_8);
        dis.reset();
        return value;
    }

    public static void readDataBase(final FileMapState state) throws IOException {
        if (state.getDataFile().length() == 0) {
            return;
        }

        InputStream currentStream = new FileInputStream(state.getDataFile());
        BufferedInputStream bufferStream = new BufferedInputStream(currentStream, BUF_SIZE);
        DataInputStream dataStream = new DataInputStream(bufferStream);

        int fileLength = (int) state.getDataFile().length();

        try {
            int position = 0;
            String key1 = readKey(dataStream);
            position += key1.getBytes(StandardCharsets.UTF_8).length;
            int offset1 = dataStream.readInt();
            int firstOffset = offset1;
            position += POS_STEP;

            while (position != firstOffset) {
                String key2 = readKey(dataStream);
                position += key2.getBytes(StandardCharsets.UTF_8).length;
                int offset2 = dataStream.readInt();
                position += POS_STEP;
                String value = readValue(dataStream, offset1, offset2, position, fileLength);
                state.getDataBase().put(key1, value);
                offset1 = offset2;
                key1 = key2;
            }
            String value = readValue(dataStream, offset1, fileLength, position, fileLength);
            state.getDataBase().put(key1, value);
        } finally {
            closeStream(dataStream);
        }

    }

    private static void closeStream(final Closeable stream) throws IOException {
        stream.close();
    }

    public static void write(final Map<String, String> dataBase, final File currentFile) throws IOException {

        OutputStream currentStream = new FileOutputStream(currentFile);
        BufferedOutputStream bufferStream = new BufferedOutputStream(currentStream, BUF_SIZE);
        DataOutputStream dataStream = new DataOutputStream(bufferStream);
        long biasing = 0;

        for (String key : dataBase.keySet()) {
            biasing += key.getBytes(StandardCharsets.UTF_8).length + POS_STEP;
        }
        List<String> values = new ArrayList<String>(dataBase.keySet().size());
        for (String key : dataBase.keySet()) {
            String value = dataBase.get(key);
            values.add(value);
            dataStream.write(key.getBytes(StandardCharsets.UTF_8));
            dataStream.writeByte(0);
            dataStream.writeInt((int) biasing);
            biasing += value.getBytes(StandardCharsets.UTF_8).length;
        }

        for (String value : values) {
            dataStream.write(value.getBytes());
        }
        closeStream(dataStream);
    }
}
