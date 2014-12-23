package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class TableHolderFactory implements TableProviderFactory {
    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param path Директория с файлами базы данных.
     * @return Объект для работы с базой данных, который будет работать в указанной директории.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     * @throws java.io.IOException      В случае ошибок ввода/вывода.
     */
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableHolder(path);
    }
}
