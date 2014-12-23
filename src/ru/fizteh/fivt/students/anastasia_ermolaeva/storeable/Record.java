package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class Record implements Storeable {
    private List<Class<?>> valuesTypes;
    private List<Object> values;
    private int columnsAmount;

    public Record(List<Class<?>> valueTypes) {
        valuesTypes = valueTypes;
        columnsAmount = valueTypes.size();
        values = new ArrayList<>();
    }

    private <T> T getFormattedObjectAt(int columnIndex, Class<T> required) {
        Utility.checkColumnIndex(columnIndex, columnsAmount);
        Utility.checkCurrentColumnType(required, values.get(columnIndex));
        return (T) (values.get(columnIndex));
    }

    /**
     * Установить значение в колонку
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @param value       - значение, которое нужно установить.
     *                    Может быть null.
     *                    Тип значения должен соответствовать декларированному типу колонки.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Тип значения не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        Utility.checkColumnIndex(columnIndex, columnsAmount);
        Object formatValue = Utility.compareColumnAndValueTypes(valuesTypes.get(columnIndex), value);
        //Object formatValue = Utility.checkValueColumnType(valuesTypes.get(columnIndex), value);
        values.add(columnIndex, formatValue);
    }

    /**
     * Возвращает значение из данной колонки, не приводя его к конкретному типу.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, без приведения типа. Может быть null.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        Utility.checkColumnIndex(columnIndex, columnsAmount);
        return values.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Integer.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Integer. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Integer.class);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Long.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Long. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Long.class);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Byte.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Byte. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Byte.class);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Float.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Float. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Float.class);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Double.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Double. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Double.class);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Boolean.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Boolean. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Boolean.class);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к String.
     *
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к String. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException                               - Неверный индекс колонки.
     */
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, String.class);
    }
}
