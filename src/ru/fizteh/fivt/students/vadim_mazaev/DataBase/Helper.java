package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.structured.Storeable;

/**
 * Class contains static methods and fields, which used in data base.
 */
public final class Helper {
    // Regexes
    public static final String ILLEGAL_TABLE_NAME_REGEX = ".*\\.|\\..*|.*(/|\\\\).*";
    public static final String IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    // Names and other constant strings.
    public static final String SIGNATURE_FILE_NAME = "signature.tsv";
    public static final int NUMBER_OF_PARTITIONS = 16;
    public static final String ENCODING = "UTF-8";
    public static final String FILE_NAME_REGEX = "([0-9]|1[0-5])\\.dat";
    public static final String DIR_NAME_REGEX = "([0-9]|1[0-5])\\.dir";
    public static final int DEFAULT_PORT = 10001;

    // Lists, maps, etc.
    // HINT: If you want to add new type, just put it into this map, list and
    // reversed map will be refreshed automatically.
    public static final Map<Class<?>, String> SUPPORTED_TYPES_TO_NAMES;
    public static final List<Class<?>> SUPPORTED_TYPES_LIST;
    static {
        Map<Class<?>, String> unitializerMap = new HashMap<>();
        unitializerMap.put(Integer.class, "int");
        unitializerMap.put(Long.class, "long");
        unitializerMap.put(Byte.class, "byte");
        unitializerMap.put(Float.class, "float");
        unitializerMap.put(Double.class, "double");
        unitializerMap.put(Boolean.class, "boolean");
        unitializerMap.put(String.class, "String");
        SUPPORTED_TYPES_TO_NAMES = Collections.unmodifiableMap(unitializerMap);
        SUPPORTED_TYPES_LIST = Collections.unmodifiableList(new ArrayList<>(unitializerMap.keySet()));
    }
    public static final Map<String, Class<?>> SUPPORTED_NAMES_TO_TYPES = inverseMap(SUPPORTED_TYPES_TO_NAMES);
    // Map of getters from Storeable interface.
    public static final Map<Class<?>, Method> GETTERS;
    static {
        Method[] methods = Storeable.class.getDeclaredMethods();
        Map<Class<?>, Method> unitializerMap = new HashMap<>();
        for (Method method : methods) {
            unitializerMap.put(method.getReturnType(), method);
        }
        GETTERS = Collections.unmodifiableMap(unitializerMap);
    }

    // Methods.
    public static void recoursiveDelete(Path file) throws IOException {
        Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    System.out.println("Exception while iterating directory.");
                    throw e;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static <K, V> Map<V, K> inverseMap(Map<K, V> map) throws IllegalArgumentException {
        Map<V, K> inversed = new HashMap<>(map.size());

        for (Entry<K, V> e : map.entrySet()) {
            if (inversed.containsKey(e.getValue())) {
                throw new IllegalArgumentException("Source map contains duplicate values");
            }
            inversed.put(e.getValue(), e.getKey());
        }

        return inversed;
    }

    public static long hashIntPairAsLong(int first, int second) {
        return (((long) first) << 32) + second;
    }

    public static int unhashFirstIntFromLong(long hash) {
        return (int) (hash >> 32);
    }

    public static int unhashSecondIntFromLong(long hash) {
        return (int) hash;
    }
}
