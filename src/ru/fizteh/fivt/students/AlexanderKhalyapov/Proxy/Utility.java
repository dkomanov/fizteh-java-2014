package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Utility {
    public static final String NULL_MSG = "Object is null";
    public static final String INVALID_FORMAT_MSG = " : invalid format";
    public static final String INVALID_SIGNATURE_MSG = "Table signature is invalid";
    public static final String INVALID_TYPES_MSG = "wrong type (invalid types list)";
    public static final String INVALID_ARGUMENTS = ": invalid list of arguments";
    public static final String INVALID_INDEX_MSG = "wrong column index";
    public static final String NOT_EXIST_MSG = " must not exist";
    public static final String WRONG_LOCATION_MSG = "Records locate in the wrong file";
    public static final String SIGNATURE_CONFLICT_MSG = " contains records incompatible with signature";
    public static final String UNDEFINED_MSG = "Some error: ";
    public static final String IO_MSG = "Some i/o error occured ";
    public static final String NOT_DIRECTORY_MSG = ": contents are not subdirectories";

    public static final String TABLE_SIGNATURE = "signature.tsv";
    public static final String ENCODING = "UTF-8";
    public static final String FORMATTER = ", ";
    public static final String NULL_STRING = "null";
    public static final String BOOL_TRUE = "true";
    public static final String BOOL_FALSE = "false";
    public static final String PARSE = "parse";
    public static final String VALID_FORMAT = ", required: [type, ..., type]";
    public static final char VALUE_START_LIMITER = '[';
    public static final char VALUE_END_LIMITER = ']';
    public static final char STRING_LIMITER = '"';
    public static final int LOWER_BOUND = 0;
    public static final int UPPER_BOUND = 16;


    private static final String ILLEGAL_TABLE_NAME_PATTERN = ".*\\.|\\..*|.*(/|\\\\).*";
    private static final Map<String, Class> VALID_TYPES = new HashMap<>();
    public static final Map<Class, String> WRAPPERS_TO_PRIMITIVE = new HashMap<>();

    static {
        String[] primitiveNames = new String[]{"int", "long", "byte", "float", "double", "boolean", "String"};
        Class[] classes = new Class[]{Integer.class, Long.class, Byte.class, Float.class, Double.class,
                Boolean.class, String.class};

        for (int i = 0; i < primitiveNames.length; i++) {
            VALID_TYPES.put(primitiveNames[i], classes[i]);
            WRAPPERS_TO_PRIMITIVE.put(classes[i], primitiveNames[i]);
        }
    }

    public static String readUtil(final RandomAccessFile dbFile, final String fileName) throws IOException {
        byte[] word;
        try {
            int wordLength = dbFile.readInt();
            if (wordLength <= 0) {
                throw new IOException(fileName + INVALID_FORMAT_MSG);
            }
            word = new byte[wordLength];
            dbFile.read(word, 0, wordLength);
            return new String(word, ENCODING);
        } catch (OutOfMemoryError e) {
            throw new DatabaseFormatException(fileName + INVALID_FORMAT_MSG);
        }
    }

    public static void writeUtil(final String word,
                                 final RandomAccessFile dbFile) throws IOException {
        dbFile.writeInt(word.getBytes(ENCODING).length);
        dbFile.write(word.getBytes(ENCODING));
    }

    public static void checkTableName(final String name) {
        if (name == null || name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException(name + INVALID_FORMAT_MSG);
        }
    }

    public static void checkDirectorySubdirs(Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path subdirectory : stream) {
                if (!Files.isDirectory(subdirectory)) {
                    throw new DatabaseIOException(directory.getFileName() + NOT_DIRECTORY_MSG);
                }
            }
        } catch (IOException e) {
            throw new DatabaseIOException("Can't go through directory: " + directory.getFileName());
        }
    }

    public static void checkDirectorySubdirectories(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path subdirectory : stream) {
                if (!Files.isDirectory(subdirectory)) {
                    throw new DatabaseFormatException(directory.getFileName() + NOT_DIRECTORY_MSG);
                }
            }
        }
    }

    public static void checkTableDirectoryContent(Path tableDirectory) throws IOException {
        Path tableSignaturePath = tableDirectory.resolve(TABLE_SIGNATURE);
        if (!Files.exists(tableSignaturePath)) {
            throw new DatabaseFormatException(INVALID_SIGNATURE_MSG);
        }
        boolean signatureExists = false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tableDirectory)) {
            for (Path directoryFile : stream) {
                if (!Files.isDirectory(directoryFile)) {
                    if (signatureExists) {
                        throw new DatabaseFormatException(ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy.DBTable.FILE_TYPE
                                + directoryFile.getFileName() + NOT_EXIST_MSG);
                    } else {
                        if (!directoryFile.toAbsolutePath().equals(tableSignaturePath.toAbsolutePath())) {
                            throw new DatabaseFormatException(DBTable.FILE_TYPE
                                    + directoryFile.getFileName() + NOT_EXIST_MSG);
                        } else {
                            signatureExists = true;
                        }
                    }
                }
            }
        } catch (DirectoryIteratorException e) {
            throw e.getCause();
        }
    }

    public static void fillSignature(Path tableDirectory, List<Class<?>> signature) throws IOException {
        Path tableSignaturePath = tableDirectory.resolve(TABLE_SIGNATURE);
        try (RandomAccessFile readSig = new RandomAccessFile(tableSignaturePath.toString(), "r")) {
            if (readSig.length() > 0) {
                while (readSig.getFilePointer()
                        < readSig.length()) {
                    String types = readSig.readLine();
                    String[] typesNames = types.trim().split(Interpreter.PARAM_DELIMITER);
                    int i = 0;
                    for (String s : typesNames) {
                        if (VALID_TYPES.containsKey(s)) {
                            signature.add(i, VALID_TYPES.get(s));
                            i += 1;
                        } else {
                            throw new DatabaseFormatException(INVALID_SIGNATURE_MSG);
                        }
                    }
                }
            } else {
                throw new DatabaseFormatException(INVALID_SIGNATURE_MSG);
            }
        }
    }

    public static void recursiveDelete(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw e;
                    }
                }
            });
        } catch (IOException | SecurityException e) {
            throw new DatabaseIOException(e.getMessage());
        }
    }

    public static void recursiveDeleteCopy(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e)
                    throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw e;
                }
            }
        });
    }

    public static void checkColumnIndex(int columnIndex, int columnsAmount) {
        if (!((columnIndex >= 0) && (columnIndex <= columnsAmount))) {
            throw new IndexOutOfBoundsException(INVALID_INDEX_MSG);
        }
    }

    public static void checkCurrentColumnType(Class<?> required, Object actual) {
        if (actual == null) {
            if (!required.equals(String.class)) {
                throw new
                        ColumnFormatException(getIncompatibleTypesErrMessage(NULL_STRING, required));
            }
        } else {
            if (!required.isInstance(actual)) {
                throw new
                        ColumnFormatException(getIncompatibleTypesErrMessage(actual.toString(), required));
            }
        }
    }

    public static void checkTableColumnTypes(List<Class<?>> columnTypes) {
        if (columnTypes == null) {
            throw new IllegalArgumentException(INVALID_TYPES_MSG);
        }
        for (Class<?> columnType : columnTypes) {
            if (!WRAPPERS_TO_PRIMITIVE.containsKey(columnType)) {
                throw new IllegalArgumentException(INVALID_TYPES_MSG);
            }
        }
    }

    public static void checkIfObjectsNotNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new
                        IllegalArgumentException(NULL_MSG);
            }
        }
    }

    public static void checkBoundsForFileNames(int number, String suffix, String type) {
        if (!(number >= LOWER_BOUND && number < UPPER_BOUND)) {
            throw new DatabaseFormatException(type
                    + number + suffix + NOT_EXIST_MSG);
        }
    }

    public static List<Class<?>> fillTypes(List<String> list) {
        List<Class<?>> result = new ArrayList<>();
        for (String s : list) {
            try {
                if (VALID_TYPES.containsKey(s)) {
                    result.add(VALID_TYPES.get(s));
                } else {
                    result.add(Class.forName(s));
                }
            } catch (ClassNotFoundException e) {
                throw new ColumnFormatException(INVALID_TYPES_MSG);
            }
        }
        return result;
    }


    public static List<String> parseString(String value) throws ParseException {
        if (!(value.charAt(0) == VALUE_START_LIMITER
                && value.charAt(value.length() - 1) == VALUE_END_LIMITER)) {
            throw new ParseException(value
                    + INVALID_FORMAT_MSG + VALID_FORMAT, 0);
        }
        List<String> recordStrings = new ArrayList<>();
        String[] contents = value.substring(1, value.length() - 1).trim().split(FORMATTER);
        StringBuilder tool = new StringBuilder();
        String substring;
        int i = 0;
        while (i < contents.length) {
            substring = contents[i];
            if (substring.indexOf(STRING_LIMITER) >= 0) {
                if (substring.indexOf(STRING_LIMITER) == substring.lastIndexOf(STRING_LIMITER)) {
                    tool.append(substring);
                    tool.append(FORMATTER);
                    i += 1;
                    substring = contents[i];
                    while (substring.indexOf(STRING_LIMITER) < 0) {
                        tool.append(substring);
                        tool.append(FORMATTER);
                        i += 1;
                        substring = contents[i];
                    }
                    tool.append(substring);
                    i += 1;
                    recordStrings.add(tool.toString());
                    tool = new StringBuilder();
                } else {
                    recordStrings.add(substring);
                    i += 1;
                }
            } else {
                recordStrings.add(substring);
                i += 1;
            }
        }
        return recordStrings;
    }

    public static List<Object> formatStringValues(Table table, List<String> valuesList) throws ParseException {
        int i = 0;
        List<Object> formattedValues = new ArrayList<>();
        for (String s : valuesList) {
            if (table.getColumnType(i).equals(String.class)) {
                if (s.indexOf(STRING_LIMITER) == 0
                        && s.lastIndexOf(STRING_LIMITER) == s.length() - 1) {
                    String normalString = s.substring(1, s.length() - 1);
                    formattedValues.add(i, normalString);
                    i += 1;
                } else {
                    if (s.equals(NULL_STRING)) {
                        formattedValues.add(i, s);
                        i += 1;
                    } else {
                        throw new
                                ParseException(getIncompatibleTypesErrMessage(s, String.class), 0);
                    }
                }
            } else {
                formattedValues.add(i, s);
                i += 1;
            }
        }
        return formattedValues;
    }

    public static List<String> getStoreableValues(Table table, Storeable value) {
        int tableColumnsAmount = table.getColumnsCount();
        List<String> storeableValues = new ArrayList<>();
        Method[] methods = value.getClass().getDeclaredMethods();
        Map<Class<?>, Method> getMethods = new HashMap<>();
        for (Method method : methods) {
            getMethods.put(method.getReturnType(), method);
        }
        for (int i = 0; i < tableColumnsAmount; i++) {
            Class currentColumnType = table.getColumnType(i);
            try {
                Object columnValue = getMethods.get(currentColumnType).invoke(value, i);
                if (columnValue == null) {
                    storeableValues.add(NULL_STRING);
                } else {
                    if (columnValue.getClass().equals(String.class)) {
                        storeableValues.add(String.valueOf(STRING_LIMITER) + columnValue.toString() + STRING_LIMITER);
                    } else {
                        storeableValues.add(String.valueOf(columnValue));
                    }
                }
            } catch (InvocationTargetException e) {
                throw new ColumnFormatException(e.getTargetException().getMessage());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(UNDEFINED_MSG + e.getMessage(), e);
            }
        }
        return storeableValues;
    }

    private static String getIncompatibleTypesErrMessage(String currentValue, Class<?> required) {
        return "wrong type (value " + currentValue
                + " can't be applied to "
                + WRAPPERS_TO_PRIMITIVE.get(required) + ")";
    }

    public static Object compareColumnAndValueTypes(Class<?> required, Object value) {
        String currentValue = String.valueOf(value);
        if (currentValue.equals(NULL_STRING)) {
            if (!required.equals(String.class)) {
                throw new
                        ColumnFormatException(getIncompatibleTypesErrMessage(NULL_STRING, required));
            } else {
                return null;
            }
        } else {
            if (required.equals(String.class)) {
                if (value.getClass().equals(String.class)) {
                    return currentValue;
                } else {
                    throw new
                            ColumnFormatException(getIncompatibleTypesErrMessage(currentValue, required));
                }
            } else {
                if (required.equals(Boolean.class)) {
                    if (currentValue.equals(BOOL_TRUE)) {
                        return true;
                    } else {
                        if (currentValue.equals(BOOL_FALSE)) {
                            return false;
                        } else {
                            throw new
                                    ColumnFormatException(getIncompatibleTypesErrMessage(currentValue, required));
                        }
                    }
                } else {
                    if (required.equals(Integer.class)) {
                        try {
                            return Integer.parseInt(currentValue);
                        } catch (NumberFormatException n) {
                            throw new
                                    ColumnFormatException(getIncompatibleTypesErrMessage(currentValue, required));
                        }
                    } else {
                        StringBuilder parseMethodName = new StringBuilder(PARSE);
                        parseMethodName.append(required.getSimpleName());
                        try {
                            return required.getMethod(parseMethodName.toString(),
                                    String.class).invoke(null, currentValue);
                        } catch (InvocationTargetException e) {
                            throw new
                                    ColumnFormatException(getIncompatibleTypesErrMessage(currentValue, required));
                        } catch (NoSuchMethodException | IllegalAccessException e) {
                            throw new IllegalArgumentException(UNDEFINED_MSG + e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    public static Function<String[], String[]> getStringHandlerForCreate() {
        return (String[] strings) -> {
            String firstArgument = strings[1];
            if (firstArgument.contains("(")) {
                throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
            }
            StringBuilder typesList = new StringBuilder();
            int i = 0;
            while ((i < strings.length) && !strings[i].contains("(")) {
                i++;
            }
            if (i != 2) {
                throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
            }
            while (i < strings.length - 1) {
                typesList.append(strings[i]);
                typesList.append(" ");
                i++;
            }
            typesList.append(strings[i]);
            int length = typesList.length();
            if (!(typesList.toString().charAt(0) == '(')
                    && !(typesList.toString().charAt(length - 1) == ')')) {
                throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
            }
            return new String[]{strings[0], strings[1], typesList.toString()};
        };

    }
    public static Function<String[], String[]> getStringHandlerForPut() {
        return (String[] strings) -> {
            String firstArgument = strings[1];
            if (firstArgument.contains("[")) {
                throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
            }
            StringBuilder typesList = new StringBuilder();
            int i = 0;
            while ((i < strings.length) && !strings[i].contains("[")) {
                i++;
            }
            if (i != 2) {
                throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
            }
            while (i < strings.length - 1) {
                typesList.append(strings[i]);
                typesList.append(" ");
                i++;
            }
            typesList.append(strings[i]);
            return new String[]{strings[0], strings[1], typesList.toString()};
        };
    }
}
