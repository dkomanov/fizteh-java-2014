package ru.fizteh.fivt.students.deserg.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by deserg on 03.12.14.
 */
public class TableRow implements Storeable {

    private ArrayList<Object> columns = new ArrayList<>();
    private List<Class<?>> signature = new LinkedList<>();

    public List<Class<?>> getSignature() {
        return signature;
    }

    public ArrayList<Object> getColumns() {
        return columns;
    }


    public TableRow(List<Class<?>> signature) {
        this.signature = signature;
        for (int i = 0; i < signature.size(); i++) {
            columns.add(null);
        }
        //columns.setSize(signature.size());
    }

    /**
     * Установить значение в колонку
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @param value - значение, которое нужно установить.
     *              Может быть null.
     *              Тип значения должен соответствовать декларированному типу колонки.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Тип значения не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: setColumnAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (value == null || value.getClass() == signature.get(columnIndex)) {
            columns.set(columnIndex, value);
        } else {
            throw new ColumnFormatException("TableRow: setColumnAt: value has an incorrect format");
        }

    }

    /**
     * Возвращает значение из данной колонки, не приводя его к конкретному типу.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, без приведения типа. Может быть null.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getColumnAt: index \"" + columnIndex + "\" out of bounds");
        }

        return columns.get(columnIndex);
    }


    /**
     * Возвращает значение из данной колонки, приведя его к Integer.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Integer. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (Integer.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (Integer) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Long.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Long. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (Long.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (Long) columns.get(columnIndex);

    }

    /**
     * Возвращает значение из данной колонки, приведя его к Byte.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Byte. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (Byte.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (Byte) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Float.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Float. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (Float.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (Float) columns.get(columnIndex);

    }

    /**
     * Возвращает значение из данной колонки, приведя его к Double.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Double. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (Double.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (Double) columns.get(columnIndex);

    }

    /**
     * Возвращает значение из данной колонки, приведя его к Boolean.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Boolean. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (Boolean.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (Boolean) columns.get(columnIndex);

    }

    /**
     * Возвращает значение из данной колонки, приведя его к String.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к String. Может быть null.
     * @throws ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("TableRow: getIntAt: index \"" + columnIndex + "\" out of bounds");
        }

        if (String.class != signature.get(columnIndex)) {
            throw new ColumnFormatException("TableRow: getIntAt: value has an incorrect format");
        }

        return (String) columns.get(columnIndex);

    }


    @Override
    public int hashCode() {
        int result = columns != null ? columns.hashCode() : 0;
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        return result;
    }

    /**
     * Not-interface methods begin here
     */
    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (!(other instanceof TableRow)) {
            return false;
        }

        TableRow otherRow = (TableRow) other;

        return (this.signature.equals(otherRow.getSignature()) && this.columns.equals(otherRow.getColumns()));
    }

}
