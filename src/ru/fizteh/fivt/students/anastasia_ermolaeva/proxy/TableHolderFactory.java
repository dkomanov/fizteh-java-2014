package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableHolderFactory implements TableProviderFactory, AutoCloseable {
    private boolean valid;
    private List<TableHolder> createdTableHolders;

    public TableHolderFactory() {
        valid = true;
        createdTableHolders = new ArrayList<>();
    }

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
        if (!valid)
            throw new IllegalStateException("Factory was closed\n");
        Utility.checkIfObjectsNotNull(path);
        TableHolder newTableHolder = new TableHolder(path);
        createdTableHolders.add(newTableHolder);
        return newTableHolder;
    }

    @Override
    public void close() throws Exception {
        createdTableHolders.forEach(TableHolder::close);
        valid = false;
    }

}
