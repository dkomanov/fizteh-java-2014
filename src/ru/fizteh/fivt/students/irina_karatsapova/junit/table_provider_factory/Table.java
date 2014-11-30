package ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory;

import java.util.List;

public interface Table {

    /**
     * Возвращает название базы данных.
     */
    String getName();

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    String get(String key);

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ.
     * @param value Значение.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     */
    String put(String key, String value);

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    String remove(String key);

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    int size();
    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    int commit() throws Exception;

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    int rollback() throws Exception;

    /**
     * Выводит список ключей таблицы
     *
     * @return Список ключей.
     */
    List<String> list();

    int changesNumber();
}
