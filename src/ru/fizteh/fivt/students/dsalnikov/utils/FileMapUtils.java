package ru.fizteh.fivt.students.dsalnikov.utils;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class FileMapUtils {
    public static Map<String, String> readToMap(File file) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (file.length() == 0) {
            return map;
        }
        try {
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is, 4096);
            DataInputStream dis = new DataInputStream(bis);

            int fileLength = (int) file.length();

            try {
                int position = 0;
                String key1 = readKey(dis);
                position += key1.getBytes(StandardCharsets.UTF_8).length;
                int offset1 = dis.readInt();
                int firstOffset = offset1;
                position += 5;

                while (position != firstOffset) {
                    String key2 = readKey(dis);
                    position += key2.getBytes(StandardCharsets.UTF_8).length;
                    int offset2 = dis.readInt();
                    position += 5;
                    String value = readValue(dis, offset1, offset2, position, fileLength);
                    map.put(key1, value);
                    offset1 = offset2;
                    key1 = key2;
                }
                String value = readValue(dis, offset1, fileLength, position, fileLength);
                map.put(key1, value);
            } finally {
                FileUtils.closeStream(dis);
            }

        } catch (IOException e) {
            throw new IOException("cannot readToMap '" + file.getName() + "'", e);
        }
        return map;
    }

    private static String readKey(DataInputStream dis) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte b = dis.readByte();
        int length = 0;
        while (b != 0) {
            bos.write(b);
            b = dis.readByte();
            length++;
            if (length > 1024 * 1024) {
                throw new IOException("wrong data format");
            }
        }
        if (length == 0) {
            throw new IOException("wrong data format");
        }

        String key = bos.toString(StandardCharsets.UTF_8.toString());

        return key;
    }

    private static String readValue(DataInputStream dis, int offset1,
                                    int offset2, int position, int length) throws IOException {
        dis.mark(length);
        dis.skip(offset1 - position);
        byte[] buffer = new byte[offset2 - offset1];
        dis.read(buffer);
        String value = new String(buffer, StandardCharsets.UTF_8);
        dis.reset();
        return value;
    }

    public static void flush(File file, Map<String, String> map) throws IOException {
        try {
            File tmp = file.getParentFile();
            tmp.mkdirs();
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os, 4096);
            DataOutputStream dos = new DataOutputStream(bos);

            try {

                long offset = 0;
                for (String key : map.keySet()) {
                    offset += key.getBytes(StandardCharsets.UTF_8).length + 5;
                }

                List<String> values = new ArrayList<>(map.keySet().size());
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    values.add(value);
                    dos.write(key.getBytes(StandardCharsets.UTF_8));
                    dos.write('\0');
                    dos.writeInt((int) offset);
                    offset += value.getBytes(StandardCharsets.UTF_8).length;
                }

                for (String value : values) {
                    dos.write(value.getBytes());
                }
            } finally {
                FileUtils.closeStream(dos);
            }

        } catch (IOException e) {
            throw new IOException("cannot flush '" + file.getName() + "'", e);
        }
    }

    private static void makeUpParsedMap(Map<String, String>[][] mapReadyForWrite, Map<String, Storeable> dataBase,
                                        Table table, TableProvider provider) {
        for (String key : dataBase.keySet()) {
            int hashCode = key.hashCode();
            hashCode *= Integer.signum(hashCode);
            int indexDir = hashCode % 16;
            int indexDat = hashCode / 16 % 16;
            if (mapReadyForWrite[indexDir][indexDat] == null) {
                mapReadyForWrite[indexDir][indexDat] = new HashMap<>();
            }
            mapReadyForWrite[indexDir][indexDat].put(key, provider.serialize(table, dataBase.get(key)));
        }
    }

    private static void cleanEmptyDirs(File fileDir) {
        if (fileDir.exists()) {
            if (fileDir.listFiles().length == 0) {
                try {
                    FileUtils.forceRemoveDirectory(fileDir);
                } catch (Exception exc) {
                    System.err.println(exc.getMessage());
                }
            }
        }
    }

    public static void writeIntoFiles(File dataBaseDirectory, Map<String, Storeable> dataBase,
                                      Table table, TableProvider provider) throws IOException {
        Map<String, String>[][] mapReadyForWrite = new Map[16][16];
        makeUpParsedMap(mapReadyForWrite, dataBase, table, provider);
        for (int indexDir = 0; indexDir < 16; ++indexDir) {
            File fileDir = new File(dataBaseDirectory, indexDir + ".dir");
            for (int indexDat = 0; indexDat < 16; ++indexDat) {
                File fileDat = new File(fileDir, indexDat + ".dat");
                if (mapReadyForWrite[indexDir][indexDat] == null) {
                    if (fileDat.exists()) {
                        if (!fileDat.delete()) {
                            throw new IOException(fileDat.toPath().toString() + ": can not delete file");
                        }
                    }
                    continue;
                }
                if (!fileDir.exists()) {
                    if (!fileDir.mkdir()) {
                        throw new IOException(fileDat.toString() + ": can not create file, something went wrong");
                    }
                }
                if (!fileDat.createNewFile()) {
                    if (!fileDat.exists()) {
                        throw new IOException(fileDat.toString() + ": can not create file, something went wrong");
                    }
                }
                writeIntoStorableFile(fileDat, mapReadyForWrite[indexDir][indexDat]);
            }
            cleanEmptyDirs(fileDir);
        }
    }

    private static List<Class<?>> getListOfTypes(File dataBaseDirectory) throws ParseException {
        final File signature = new File(dataBaseDirectory, "signature.tsv");
        try {
            Scanner scanner = new Scanner(new FileInputStream(signature));
            if (!scanner.hasNextLine()) {
                throw new IOException("signature.tsv file is empty");
            }
            String[] types = scanner.nextLine().split("\\s");
            List<Class<?>> result = new ArrayList<>();
            for (Integer i = 0; i < types.length; ++i) {
                switch (types[i]) {
                    case "int":
                        result.add(Integer.class);
                        break;
                    case "long":
                        result.add(Long.class);
                        break;
                    case "byte":
                        result.add(Byte.class);
                        break;
                    case "float":
                        result.add(Float.class);
                        break;
                    case "double":
                        result.add(Double.class);
                        break;
                    case "boolean":
                        result.add(Boolean.class);
                        break;
                    case "String":
                        result.add(String.class);
                        break;
                    default:
                        throw new IOException("signature.tsv has a bad symbols");
                }
            }
            scanner.close();
            return result;
        } catch (IOException exc) {
            throw new ParseException("read signature failed", 11);
        }
    }

    public static void readIntoDataBase(final File dataBaseDirectory, Map<String, Storeable> map, StorableTable table,
                                        TableProvider provider) throws IOException, ParseException {
        if (!dataBaseDirectory.exists() || dataBaseDirectory.isFile()) {
            throw new IOException(dataBaseDirectory + ": not directory or not exist");
        }

        List<Class<?>> types = getListOfTypes(dataBaseDirectory);
        table.useColumnTypes(types);

        for (int index = 0; index < 16; ++index) {
            String indexDirectoryName = index + ".dir";
            File indexDirectory = new File(dataBaseDirectory, indexDirectoryName);
            if (!indexDirectory.exists()) {
                continue;
            }
            if (!indexDirectory.isDirectory()) {
                throw new IOException(indexDirectory.toString() + ": not directory");
            }
            if (indexDirectory.list().length == 0) {
                throw new IOException(indexDirectory.toString() + ": is empty");
            }
            for (int fileIndex = 0; fileIndex < 16; ++fileIndex) {
                String fileIndexName = fileIndex + ".dat";
                File fileIndexDat = new File(indexDirectory, fileIndexName);
                if (!fileIndexDat.exists()) {
                    continue;
                }
                if (fileIndexDat.length() == 0) {
                    throw new IOException(fileIndexDat.toString() + ": is empty");
                }
                readIntoStoreableMap(fileIndexDat, map, table, provider, index, fileIndex);
            }
        }

    }

    public static void createSignatureFile(File tableStorageDirectory, Table table) {
        try {
            File signatureFile = new File(tableStorageDirectory, "signature.tsv");
            if (!signatureFile.exists()) {
                signatureFile.createNewFile();
            }
            BufferedWriter signatureWriter = new BufferedWriter(new FileWriter(signatureFile));
            Integer countOfColumns = table.getColumnsCount();
            for (Integer i = 0; i < countOfColumns; ++i) {
                Class<?> type = table.getColumnType(i);
                switch (type.getCanonicalName()) {
                    case "java.lang.Integer":
                        signatureWriter.write("int");
                        break;
                    case "java.lang.Long":
                        signatureWriter.write("long");
                        break;
                    case "java.lang.Byte":
                        signatureWriter.write("byte");
                        break;
                    case "java.lang.Float":
                        signatureWriter.write("float");
                        break;
                    case "java.lang.Double":
                        signatureWriter.write("double");
                        break;
                    case "java.lang.Boolean":
                        signatureWriter.write("boolean");
                        break;
                    case "java.lang.String":
                        signatureWriter.write("String");
                        break;
                    default:
                        throw new IOException("signature.tsv creation: something went wrong");
                }
                signatureWriter.write(" ");
            }
            signatureWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<?>> createListOfTypes(List<String> args) throws IOException {
        if (args.size() < 3) {
            throw new IllegalArgumentException("wrong amount of arguments in create function. At least 3 expected");
        }
        return createListOfTypesFromString(args.get(2));
    }

    public static List<Class<?>> createListOfTypesFromString(String stringTypes) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        //List<String> types = StoreableCmdParseAndExecute.splitByDelimiter(stringTypes, " ");
        String[] types = stringTypes.split(" ");
        for (String type : types) {
            switch (type) {
                case "int":
                    result.add(Integer.class);
                    break;
                case "long":
                    result.add(Long.class);
                    break;
                case "byte":
                    result.add(Byte.class);
                    break;
                case "float":
                    result.add(Float.class);
                    break;
                case "double":
                    result.add(Double.class);
                    break;
                case "boolean":
                    result.add(Boolean.class);
                    break;
                case "String":
                    result.add(String.class);
                    break;
                default:
                    throw new IOException("wrong type (input args has a bad symbols in types specification)");
            }
        }
        return result;
    }

    private static boolean checkKeyPlacement(int indexDir, int indexDat, String key) {
        int hashCode = key.hashCode();
        hashCode *= Integer.signum(hashCode);
        int dir = hashCode % 16;
        int dat = hashCode / 16 % 16;
        return (dir == indexDir && dat == indexDat);
    }

    public static void readIntoStoreableMap(File dataBaseFile, Map<String, Storeable> map, Table table,
                                            TableProvider provider, int indexDir, int indexDat)
            throws IOException, ParseException {
        RandomAccessFile dataBaseFileReader = new RandomAccessFile(dataBaseFile, "rw");
        long length = dataBaseFile.length();
        byte[] buffer;

        while (length > 0) {
            int keyLength = dataBaseFileReader.readInt();
            length -= 4;
            int valueLength = dataBaseFileReader.readInt();
            length -= 4;

            buffer = new byte[keyLength];
            dataBaseFileReader.readFully(buffer);
            length -= buffer.length;
            String key = new String(buffer, StandardCharsets.UTF_8);

            if (!checkKeyPlacement(indexDir, indexDat, key)) {
                throw new IOException("wrong key placement");
            }

            buffer = new byte[valueLength];
            dataBaseFileReader.readFully(buffer);
            length -= buffer.length;
            String value = new String(buffer, StandardCharsets.UTF_8);

            map.put(key, provider.deserialize(table, value));
        }
        dataBaseFileReader.close();
    }

    public static void writeIntoStorableFile(File dataBaseFile, Map<String, String> map) throws IOException {
        try (RandomAccessFile dataBaseFileWriter = new RandomAccessFile(dataBaseFile, "rw")) {
            dataBaseFileWriter.setLength(0);
            for (Map.Entry<String, String> element : map.entrySet()) {
                String key = element.getKey();
                byte[] bufferKey = key.getBytes(StandardCharsets.UTF_8);
                dataBaseFileWriter.writeInt(bufferKey.length);

                String value = element.getValue();
                byte[] bufferValue = value.getBytes(StandardCharsets.UTF_8);
                dataBaseFileWriter.writeInt(bufferValue.length);

                dataBaseFileWriter.write(bufferKey);
                dataBaseFileWriter.write(bufferValue);
            }
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
    }
}
