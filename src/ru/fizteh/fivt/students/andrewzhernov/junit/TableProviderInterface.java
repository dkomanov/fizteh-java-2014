package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.util.Map;

public interface TableProviderInterface {

    /**
     * Возвращает таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    Table getTable(String name);

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    Table createTable(String name);

    /**
     * Удаляет таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     */
    void removeTable(String name);

    /**
     * Устанавливает таблицу с указанным названием в качестве текущей.
     *
     * @param name Название таблицы.
     * @return Имя текущей таблицы. Если таблица не выбрана, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если предыдущая таблица имеет несохранённые изменения.
     */
    String useTable(String name);

    /**
     * Список таблиц с их размером.
     *
     * @return Структура данных ключ-значение, где ключ - имя таблицы, значение - количество ключей, которые в ней хранятся.
     */
    Map<String, Integer> showTables();

    /**
     * Выход.
     *
     * @throws IllegalStateException Если текущая таблица имеет несохранённые изменения.
     */
    void exit();
}
