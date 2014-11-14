package ru.fizteh.fivt.storage.structured;

import java.io.IOException;

/**
 * Представляет интерфейс для подключения к удаленному хранилищу.
 *
 * Предполагается, что реализация интерфейса клиента будет иметь публичный конструктор без параметров.
 */
public interface RemoteTableProviderFactory {
    /**
     * Возвращает {@link ru.fizteh.fivt.storage.structured.RemoteTableProvider} для работы с удаленных
     * хранилищем.
     * @param hostname
     *         Имя узла, на котором работает TCP-сервер хранилища.
     * @param port
     *         Порт, на котором работает TCP-сервер хранилища.
     * @return Remote-{@link ru.fizteh.fivt.storage.structured.RemoteTableProvider}, который подключен к
     * TCP-серверу хранилища по заданному адресу.
     * @throws IllegalArgumentException
     *         В случае некорректных входных параметров.
     * @throws java.io.IOException
     *         В случае ошибок ввода/вывода.
     */
    RemoteTableProvider connect(String hostname, int port) throws IOException;
}
