package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONHelper.*;

/**
 * This class helps to construct a JSON string from any object using special annotations.
 * @author Phoenix
 * @see JSONComplexObject
 * @see JSONField
 */
public final class JSONMaker {
    private JSONMaker() { }

    private static List<Field> getAnnotatedFields(Class<?> searchableClass,
                                                  Class<? extends Annotation> annoClass) {
        List<Field> gatheredFields = new LinkedList<>();

        while (searchableClass != Object.class) {
            Arrays.stream(searchableClass.getDeclaredFields()).forEach(
                    (field) -> {
                        if (field.getAnnotation(annoClass) != null) {
                            gatheredFields.add(field);
                        }
                    });
            searchableClass = searchableClass.getSuperclass();
        }

        return gatheredFields;
    }

    /**
     * Appends JSON interpretation of obj to the given string builder.
     * @param sb
     *         string builder for JSON.
     * @param obj
     *         object to convert to JSON. Can be null.
     * @param name
     *         if the object is named, name is printed before its contents. If null, object is considered not
     *         named.
     * @param identityMap
     *         map to put objects to solve problem of cyclic links.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static void appendJSONString(final StringBuilder sb,
                                         final Object obj,
                                         final String name,
                                         final IdentityHashMap<Object, Boolean> identityMap)
            throws IllegalArgumentException, IllegalAccessException {
        if (name != null) {
            sb.append(QUOTES).append(name).append(QUOTES).append(KEY_VALUE_SEPARATOR);
        }
        if (obj == null) {
            sb.append(NULL);
            return;
        }

        // Checking cyclic links.
        if (identityMap.containsKey(obj)) {
            sb.append(CYCLIC);
            return;
        }
        identityMap.put(obj, Boolean.TRUE);

        Class<?> objClass = obj.getClass();

        if (obj instanceof Map) {
            sb.append(OPENING_CURLY_BRACE);
            Set<Entry> set = ((Map) obj).entrySet();

            for (Entry e : set) {
                appendJSONString(sb, e.getValue(), e.getKey().toString(), identityMap);
            }

            sb.append(CLOSING_CURLY_BRACE);
        } else if (objClass.isArray() || obj instanceof Iterable) {
            sb.append(OPENING_SQUARE_BRACE);

            if (obj instanceof Iterable) {
                boolean comma = false;
                for (Object piece : (Iterable) obj) {
                    if (comma) {
                        sb.append(ELEMENT_SEPARATOR);
                    }
                    comma = true;
                    appendJSONString(sb, piece, null, identityMap);
                }
            } else {
                int length = Array.getLength(obj);
                boolean comma = false;
                for (int i = 0; i < length; i++) {
                    if (comma) {
                        sb.append(ELEMENT_SEPARATOR);
                    }
                    comma = true;
                    appendJSONString(sb, Array.get(obj, i), null, identityMap);
                }
            }

            sb.append(CLOSING_SQUARE_BRACE);
        } else if (obj instanceof Number) {
            sb.append(obj.toString());
        } else if (obj instanceof Boolean) {
            sb.append(obj.toString());
        } else if (objClass.getAnnotation(JSONComplexObject.class) != null) {
            // Convenience trick.
            FieldJSONAppender jsonAppender = (field, overrideName) -> {
                String fieldName = field.getAnnotation(JSONField.class).name();
                boolean accessible = field.isAccessible();
                field.setAccessible(true);

                if (overrideName != null && overrideName.isEmpty()) {
                    if (fieldName.isEmpty()) {
                        overrideName = field.getName();
                    } else {
                        overrideName = fieldName;
                    }
                }

                appendJSONString(
                        sb, field.get(obj), overrideName, identityMap);
                field.setAccessible(accessible);
            };

            // Fields to convert to json.
            List<Field> annotatedFields = getAnnotatedFields(objClass, JSONField.class);
            if (annotatedFields.isEmpty()) {
                throw new IllegalArgumentException(
                        "Illegal annotation @JSONComplexObject: there are no @JSONField annotated fields");
            } else if (objClass.getAnnotation(JSONComplexObject.class).singleField()) {
                if (annotatedFields.size() > 1) {
                    throw new IllegalArgumentException(
                            "Illegal annotation @JSONComplexObject: there are more then one @JSONField");
                }
                jsonAppender.appendInfo(annotatedFields.get(0), name);

            } else {
                sb.append(OPENING_CURLY_BRACE);
                boolean comma = false;

                for (Field field : annotatedFields) {
                    if (comma) {
                        sb.append(ELEMENT_SEPARATOR);
                    }
                    comma = true;
                    jsonAppender.appendInfo(field);
                }
                sb.append(CLOSING_CURLY_BRACE);
            }
        } else {
            sb.append(QUOTES).append(JSONHelper.escape(obj.toString())).append(QUOTES);
        }

        identityMap.remove(obj);
    }

    /**
     * Serializes an object using JSON-style.<br/>
     * If cyclic link found, 'cyclic' is printed instead of cyclic description of the object.<br/>
     * <ul>
     * <li>Arrays and Iterables are supported</li>
     * <li>Maps are supported (keys are treated as string names)</li>
     * <li>Numbers and Strings are supported</li>
     * <li>Complex objects are supported</li>
     * <li>{@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONComplexObject} instances
     * are
     * supported</li>
     * </ul>
     * Objects that do not conform to any of categories above are converted to strings.
     * @param object
     *         object to serialize.
     * @return JSON string.
     * @throws RuntimeException
     *         {@link IllegalAccessException } can be wrapped in it.
     * @see Iterable
     * @see Object#toString()
     * @see Number
     * @see JSONComplexObject
     * @see JSONField
     */
    public static String makeJSON(Object object) throws RuntimeException {
        try {
            StringBuilder sb = new StringBuilder();
            appendJSONString(sb, object, null, new IdentityHashMap<>());
            return sb.toString();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Convenience structure used in method {@link #appendJSONString(StringBuilder, Object, String,
     * java.util.IdentityHashMap)}.
     */
    @FunctionalInterface
    private interface FieldJSONAppender {
        void appendInfo(Field field, String overrideName) throws IllegalAccessException;

        default void appendInfo(Field field) throws IllegalAccessException {
            appendInfo(field, "");
        }
    }
}
