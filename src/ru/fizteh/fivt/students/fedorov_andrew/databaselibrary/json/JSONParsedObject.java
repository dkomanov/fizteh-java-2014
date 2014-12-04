package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json;

import java.util.Map;

/**
 * An object decoded from a JSON string.<br/>
 * Can represent standard array or associative array (Map) - then operations with Map or standard array
 * (correspondently).<br/>
 * Fields/elements of this object can be of the following types:
 * <ul>
 * <li>{@link java.lang.Long}</li>
 * <li>{@link java.lang.Double}</li>
 * <li>{@link java.lang.Boolean}</li>
 * <li>{@link java.lang.String}</li>
 * <li>{@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParsedObject}</li>
 * </ul>
 */
public interface JSONParsedObject {
    /**
     * Set some field.
     * @param name
     *         name of the field.
     * @param value
     *         value of the field. Can be null.
     * @throws java.lang.UnsupportedOperationException
     */
    void put(String name, Object value) throws UnsupportedOperationException;

    /**
     * Set some element.
     * @param index
     *         index of the element in array.
     * @param value
     *         value of the element. Can be null.
     * @throws UnsupportedOperationException
     */
    void put(int index, Object value) throws UnsupportedOperationException;

    /**
     * Retrieve some field's value (for standard objects)
     * @param name
     *         name of the field
     * @return null, {@link Integer}, {@link String}, {@link Double}, {@link Boolean} or {@link
     * JSONParsedObject}.
     * @throws java.lang.UnsupportedOperationException
     */
    Object get(String name) throws UnsupportedOperationException;

    /**
     * Retrieve some element (for arrays)
     * @param index
     *         index of the element
     * @return null, {@link Integer}, {@link String}, {@link Double}, {@link Boolean} or {@link
     * JSONParsedObject}.
     * @throws java.lang.UnsupportedOperationException
     */
    Object get(int index) throws UnsupportedOperationException, ClassCastException;

    default Long getLong(int index) throws UnsupportedOperationException, ClassCastException {
        return (Long) get(index);
    }

    default Long getLong(String name) throws UnsupportedOperationException, ClassCastException {
        return (Long) get(name);
    }

    default String getString(String name) throws UnsupportedOperationException, ClassCastException {
        return (String) get(name);
    }

    default Double getDouble(int index) throws UnsupportedOperationException, ClassCastException {
        return (Double) get(index);
    }

    default Double getDouble(String name) throws UnsupportedOperationException, ClassCastException {
        return (Double) get(name);
    }

    default String getString(int index) throws UnsupportedOperationException, ClassCastException {
        return (String) get(index);
    }

    default Boolean getBoolean(String name) throws UnsupportedOperationException, ClassCastException {
        return (Boolean) get(name);
    }

    default Boolean getBoolean(int index) throws UnsupportedOperationException, ClassCastException {
        return (Boolean) get(index);
    }

    default JSONParsedObject getObject(String name)
            throws UnsupportedOperationException, ClassCastException {
        return (JSONParsedObject) get(name);
    }

    default JSONParsedObject getObject(int index)
            throws UnsupportedOperationException, ClassCastException {
        return (JSONParsedObject) get(index);
    }

    /**
     * Returns true if represented object contains field with the given name.
     * @param name
     *         field name.
     */
    boolean containsField(String name) throws UnsupportedOperationException;

    /**
     * Returns true if the represented object is an array, false otherwise.
     */
    boolean isStandardArray();

    /**
     * Performs a chain of {@link #get(String) } and {@link #get(int) } methods on the object structure.
     * @param namePieces
     *         consists of Strings and Integers. Each name piece causes associated object retrieval.
     * @return null, {@link Integer}, {@link String}, {@link Double}, {@link Boolean} or {@link
     * JSONParsedObject}.
     */
    Object deepGet(Object... namePieces);

    /**
     * Returns count of fields/elements stored in this object/array.
     */
    int size();

    /**
     * Returns this object as map. Unsupported for standard array respresentations.
     * @throws java.lang.UnsupportedOperationException
     */
    Map<String, Object> asMap() throws UnsupportedOperationException;

    /**
     * Returns this object as array. Unsupported for associative array representations.
     * @throws java.lang.UnsupportedOperationException
     */
    Object[] asArray() throws UnsupportedOperationException;

    /**
     * Returns string representation of this object (not in JSON format).<br/>
     * Depending on the object method {@link java.util.Arrays#toString(boolean[])} or {@link
     * java.util.Map#toString()} is used.
     */
    String toString();
}
