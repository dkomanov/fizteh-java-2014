package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.Writer;

/**
 * Представляет интерфейс для создания обёрток вокруг интерфейсов.
 */
public interface LoggingProxyFactory {

    /**
     * Создаёт класс-обёртку вокруг объекта <code>implementation</code>, которая при вызове
     * методов интерфейса <code>interfaceClass</code> выполняет логирование аргументов и результата
     * вызова методов.
     *
     * Класс-обёртка не имеет права выбрасывать свои исключения, но обязана выбрасывать те же самые
     * исключения, что выбрасывает оригинальный класс.
     *
     * Класс-обёртка должен быть потокобезопасным.
     *
     * @param writer          Объект, в который ведётся запись лога.
     * @param implementation  Объект, реализующий интерфейс <code>interfaceClass</code>.
     * @param interfaceClass  Класс интерфейса, методы которого должны выполнять запись в лог.
     *
     * @return Объект, реализующий интерфейс <code>interfaceClass</code>, при вызове методов которого
     * выполняется запись в лог.
     *
     * @throws IllegalArgumentException Если любой из переданных аргументов null или имеет некорректное значение.
     */
    Object wrap(
            Writer writer,
            Object implementation,
            Class<?> interfaceClass
    );
}
