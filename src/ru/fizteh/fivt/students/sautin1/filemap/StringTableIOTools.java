package ru.fizteh.fivt.students.sautin1.filemap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by sautin1 on 10/12/14.
 */
public class StringTableIOTools implements TableIOTools<String, StringTable> {
    private int dirQuantity;
    private int fileQuantity;
    private final String encoding;
    //private final int buffSize = 8192;

    public StringTableIOTools(int dirQuantity, int fileQuantity, String encoding) {
        this.dirQuantity = dirQuantity;
        this.fileQuantity = fileQuantity;
        this.encoding = encoding;
    }

    public StringTableIOTools() {
        this(0, 1, "UTF-8");
    }

    private byte[] intToByteArray(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    private int byteArrayToInt(byte[] byteArr) {
        ByteBuffer wrapper = ByteBuffer.wrap(byteArr);
        return wrapper.getInt();
    }

    private String readEncodedString(InputStream inStream) throws IOException {
        String string = null;
        try {
            byte[] intByteBuf = new byte[4];
            int bytesRead = inStream.read(intByteBuf, 0, 4);
            if (bytesRead < 4) {
                if (bytesRead == -1) {
                    return null;
                } else {
                    throw new IOException("File is corrupted");
                }
            }
            int stringSize = byteArrayToInt(intByteBuf);
            byte[] stringByteBuf = new byte[stringSize];
            bytesRead = inStream.read(stringByteBuf, 0, stringSize);
            if (bytesRead < stringSize) {
                throw new IOException("File is corrupted");
            }
            string = new String(stringByteBuf, encoding);
        } catch (IOException e) {
            throw new IOException("Read error: " + e.getMessage());
        }
        return string;
    }

    private void writeEncodedString(OutputStream outStream, String string) throws IOException {
        try {
            byte[] stringBytes = string.getBytes(encoding);
            outStream.write(intToByteArray(stringBytes.length));
            outStream.write(stringBytes);
        } catch (IOException e) {
            throw new IOException("Write error: " + e.getMessage());
        }
    }

    @Override
    public StringTable readTable(Path rootPath, StringTable table) throws IOException {
        if (fileQuantity == 0) {
            return null;
        }
        if (dirQuantity == 0) {
            if (Files.isDirectory(rootPath)) {
                throw new IllegalArgumentException("Wrong file");
            }
            if (!Files.exists(rootPath)) {
                throw new IllegalArgumentException("File doesn't exist");
            }
            try (InputStream inStream = Files.newInputStream(rootPath)) {
                while (true) {
                    String key = readEncodedString(inStream);
                    if (key == null) {
                        break;
                    }
                    String value = readEncodedString(inStream);
                    if (value == null) {
                        throw new IOException("File is corrupted");
                    }
                    table.put(key, value);
                }
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        }
        return table;
    }

    @Override
    public void writeTable(Path rootPath, StringTable table) {
        if (fileQuantity == 0) {
            return;
        }
        if (dirQuantity == 0) {
            if (Files.isDirectory(rootPath)) {
                throw new IllegalArgumentException("Wrong file");
            }
            if (!Files.exists(rootPath)) {
                try {
                    Files.createFile(rootPath);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            try (OutputStream outStream = Files.newOutputStream(rootPath)) {
                for (Map.Entry<String, String> entry : table) {
                    writeEncodedString(outStream, entry.getKey());
                    writeEncodedString(outStream, entry.getValue());
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }


}
