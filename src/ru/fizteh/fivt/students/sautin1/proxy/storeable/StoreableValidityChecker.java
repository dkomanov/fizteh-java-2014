package ru.fizteh.fivt.students.sautin1.proxy.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

/**
 * Created by sautin1 on 12/10/14.
 */
public class StoreableValidityChecker {

    public static void checkColumnType(Class<?> type) throws ColumnFormatException {
        if (!StoreableXMLUtils.TYPE_SET.contains(type)) {
            throw new ColumnFormatException("Invalid type " + type.getSimpleName());
        }
    }

    public static void checkClassAssignable(Class<?> parentType, Class<?> sonType) throws ColumnFormatException {
        if (!parentType.isAssignableFrom(sonType)) {
            String exceptionMessage = "Type " + sonType.getSimpleName();
            exceptionMessage += " cannot be assigned to " + parentType.getSimpleName();
            throw new ColumnFormatException(exceptionMessage);
        }
    }

    public static void checkValueFormat(Table table, Storeable checkIt) throws ColumnFormatException {
        try {
            for (int typeIndex = 0; typeIndex < table.getColumnsCount(); ++typeIndex) {
                if (checkIt.getColumnAt(typeIndex) == null) {
                    continue;
                }
                Class<?> tableClass = table.getColumnType(typeIndex);
                Class<?> storeableClass = checkIt.getColumnAt(typeIndex).getClass();
                checkClassAssignable(tableClass, storeableClass);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Too few columns in Storeable");
        }
        try {
            checkIt.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            // everything is good
            return;
        }
        throw new ColumnFormatException("Too many columns in Storeable");
    }
}
