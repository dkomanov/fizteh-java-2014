package ru.fizteh.fivt.students.pavel_voropaev.project.master;

import java.io.IOException;
import java.util.List;

/**
 * @author Fedor Lavrentyev
 * @author Dmitriy Komanov
 */
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
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    String get(String key);

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     */
    String put(String key, String value);

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
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
     * @return Число записанных изменений.
     *
     * @throws java.io.IOException если произошла ошибка ввода/вывода. Целостность таблицы не гарантируется.
     */
    int commit() throws IOException;

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    int rollback();

    /**
     * Выводит список ключей таблицы
     *
     * @return Список ключей.
     */
    List<String> list();

    /**
     * Возвращает количество изменений, ожидающих фиксации.
     *
     * @return Количество изменений, ожидающих фиксации.
     */
    int getNumberOfUncommittedChanges();
}
