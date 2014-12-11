package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.students.deserg.telnet.Serializer;
import ru.fizteh.fivt.students.deserg.telnet.exceptions.MyException;
import ru.fizteh.fivt.students.deserg.telnet.exceptions.MyIOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by deserg on 01.12.14.
 */
public class FileSystem {

    public static boolean checkName(String name) {

        return (!name.contains("\\") && !name.contains("/000"));

    }

    public static void deleteContent(Path path) {

        if (!Files.isDirectory(path)) {
                throw new MyException("Database is not located in a directory");
        } else {
            deleteFinal(path, false);
        }

    }


    public static void delete(Path path) {

        if (!Files.isDirectory(path)) {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new MyException("Error while deleting " + path.toString());
            }
        } else {
            deleteFinal(path, true);
        }

    }

    private static void deleteFinal(Path path, boolean deleteParent) {

        File curDir = new File(path.toString());
        File[] content = curDir.listFiles();

        if (content != null) {
            for (File item: content) {
                if (item.isFile()) {
                    try {
                        Files.delete(item.toPath());
                    } catch (IOException ex) {
                        throw new MyException("I/O error occurs while removing " + item.toPath().toString());
                    }
                } else {
                    deleteFinal(item.toPath(), true);
                }
            }
        }

        if (deleteParent) {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new MyException("I/O error occurs while removing " + path.toString());
            }
        }
    }


    public static List<Class<?>> readSignature(Path path) throws MyIOException {

        Path sPath = path.resolve("signature.tsv");

        try (DataInputStream is = new DataInputStream(Files.newInputStream(sPath))) {

            if (is.available() == 0) {
                throw new MyIOException("File is empty");
            }

            String line = "";
            while (is.available() > 0) {
                line += is.readChar();
            }

            String[] types = line.trim().split("\t");
            List<Class<?>> list = Serializer.makeSignatureFromStrings(types);
            return list;

        } catch (IOException ex) {
            throw new MyIOException("Database: read: failed to read from \"signature.tsv\"");
        }

    }

    public static void writeSignature(List<Class<?>> signature, Path path) throws MyIOException {

        Path sPath = path.resolve("signature.tsv");
        if (Files.exists(sPath)) {
            FileSystem.delete(sPath);
        }

        try {
            Files.createFile(sPath);
        } catch (IOException ex) {
            throw new MyIOException("Database: write: failed create \"signature.tsv\"");
        }

        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(sPath))) {

            for (Class<?> cl: signature) {

                if (cl == Integer.class) {
                    os.writeChars("int\t");

                } else if (cl == Long.class) {
                    os.writeChars("long\t");

                } else if (cl == Byte.class) {
                    os.writeChars("byte\t");

                } else if (cl == Float.class) {
                    os.writeChars("float\t");

                } else if (cl == Double.class) {
                    os.writeChars("double\t");

                } else if (cl == Boolean.class) {
                    os.writeChars("boolean\t");

                } else if (cl == String.class) {
                    os.writeChars("String\t");
                } else {
                    throw new MyException("Database: write: invalid signature");
                }
            }

        } catch (IOException ex) {
            throw new MyIOException("Database: read: failed write to \"signature.tsv\"");
        }
    }

}
