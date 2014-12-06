package ru.fizteh.fivt.students.dnovikov.storeable;

import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.LoadOrSaveException;

import java.text.ParseException;
import java.util.*;

public class Utils {
    public static final Map<String, Class<?>> SUPPORTED_NAMES_TO_TYPES;
    public static final Map<Class<?>, String> SUPPORTED_TYPES_TO_NAMES;
    public static final String SIGNATURE_FILE_NAME = "signature.tsv";
    public static final String REGEXP_TO_SPLIT_JSON = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    static {
        Map<String, Class<?>> namesToTypes = new HashMap<>();
        Map<Class<?>, String> typesToNames = new HashMap<>();
        typesToNames.put(Integer.class, "int");
        typesToNames.put(Long.class, "long");
        typesToNames.put(Byte.class, "byte");
        typesToNames.put(Float.class, "float");
        typesToNames.put(Double.class, "double");
        typesToNames.put(Boolean.class, "boolean");
        typesToNames.put(String.class, "String");

        namesToTypes.put("int", Integer.class);
        namesToTypes.put("long", Long.class);
        namesToTypes.put("byte", Byte.class);
        namesToTypes.put("float", Float.class);
        namesToTypes.put("double", Double.class);
        namesToTypes.put("boolean", Boolean.class);
        namesToTypes.put("String", String.class);
        SUPPORTED_NAMES_TO_TYPES = Collections.unmodifiableMap(namesToTypes);
        SUPPORTED_TYPES_TO_NAMES = Collections.unmodifiableMap(typesToNames);
    }

    public static String getJSONStringForPut(String[] args) throws ParseException {
        if (args.length < 2) {
            throw new ParseException("put: wrong number of arguments", 0);
        }
        String result = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return result;
    }

    public static void tryToChangeUsedCurrentTable(DataBaseProvider dbConnector, String name) {
        DataBaseTable currentTable = dbConnector.getCurrentTable();
        int unsavedChanges = currentTable.getNumberOfUncommittedChanges();
        if (dbConnector.getTable(name) != null) {
            if (unsavedChanges > 0) {
                System.out.println(unsavedChanges + " unsaved changes");
            } else {
                try {
                    currentTable.save();
                    dbConnector.setCurrentTable(dbConnector.getTable(name));
                    System.out.println("using " + name);
                } catch (LoadOrSaveException e) {
                    System.err.println(e.getMessage());
                }
            }
        } else {
            System.out.println(name + " not exists");
        }
    }

    public static List<Class<?>> getTypesForCreate(String[] args) throws ParseException {
        if (args.length < 2) {
            throw new ParseException("create: wrong number of arguments", 0);
        }
        String stringTypes = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (!stringTypes.contains("(") || !stringTypes.contains(")")) {
            throw new ParseException("can't create table '" + args[0] + "': wrong format of types", 0);
        }
        if (stringTypes.indexOf("(") != 0 || stringTypes.indexOf(")") != stringTypes.length() - 1) {
            throw new ParseException("can't create table '" + args[0] + "': wrong format of types", 0);
        }
        if (!stringTypes.startsWith("(")) {
            throw new ParseException("can't create table '" + args[0] + "': arguments doesn't start with '('", 0);
        }
        if (!stringTypes.endsWith(")")) {
            throw new ParseException("can't create table '" + args[0] + "': arguments doesn't end with ')", 0);
        }
        String[] types = stringTypes.substring(1, stringTypes.length() - 1).split(" ");
        List<Class<?>> typesList = new ArrayList<>();
        for (String type : types) {
            Class<?> typeClass = Utils.SUPPORTED_NAMES_TO_TYPES.get(type);
            if (typeClass != null) {
                typesList.add(typeClass);
            } else {
                throw new ParseException("unsupported type: '" + type + "'", 0);
            }
        }
        return typesList;
    }
}
