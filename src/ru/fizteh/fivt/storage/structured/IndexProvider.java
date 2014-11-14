package ru.fizteh.fivt.storage.structured;

/**
 * Дополнительный интерфейс к {@link ru.fizteh.fivt.storage.structured.TableProvider}, позволяющий создавать
 * индексы на
 * основе имеющейся таблицы.
 */
public interface IndexProvider extends TableProvider {
    /**
     * Возвращает индекс с таким именем, если он существует.
     * @param name
     *         Имя индекса. Не должно быть null.
     * @return Объект индекса
     * @throws IllegalArgumentException
     *         Если имя индекса невалидно.
     */
    Index getIndex(String name) throws IllegalArgumentException;

    /**
     * Создает индекс.
     * @param table
     *         Таблица, для которой нужно создать индекс.
     * @param column
     *         Номер колонки.
     * @param name
     *         Имя для будущего индекса.
     * @return Свежесозданный индекс. null, если индекс уже был создан.
     * @throws IllegalArgumentException
     *         Если таблица невалидна, указана неверная колонка или имя индекса,
     *         а также, если сущность с таким именем уже существует.
     * @throws IllegalStateException
     *         Если индекс содержит невалидные элементы.
     */
    Index createIndex(Table table, int column, String name)
            throws IllegalArgumentException, IllegalStateException;
}
