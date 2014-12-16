package ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings;

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
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    String get(String key) throws IllegalArgumentException;

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
    String put(String key, String value) throws IllegalArgumentException;

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    String remove(String key) throws IllegalArgumentException;

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    int size();

    /**
    * Возвращает количество изменений в таблице.
    *
    * @return Количество изменений в таблице.
    */
    int getNumberOfUncommittedChanges();

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    int commit();

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
}

