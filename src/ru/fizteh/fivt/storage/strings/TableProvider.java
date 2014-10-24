package ru.fizteh.fivt.storage.strings;

/**
 * @author Fedor Lavrentyev
 * @author Dmitriy Komanov
 */
public interface TableProvider {
    /**
     * Возвращает таблицу с указанным названием.
     * @param name
     *         Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует,
     * возвращает null.
     * @throws IllegalArgumentException
     *         Если название таблицы null или имеет недопустимое значение.
     */
    Table getTable(String name) throws IllegalArgumentException;

    /**
     * Создаёт таблицу с указанным названием.
     * @param name
     *         Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException
     *         Если название таблицы null или имеет недопустимое значение.
     */
    Table createTable(String name) throws IllegalArgumentException;

    /**
     * Удаляет таблицу с указанным названием.
     * @param name
     *         Название таблицы.
     * @throws IllegalArgumentException
     *         Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException
     *         Если таблицы с указанным названием не существует.
     */
    void removeTable(String name) throws IllegalArgumentException;
}
