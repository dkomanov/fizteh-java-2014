package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by luba_yaronskaya on 26.10.14.
 */
public class JUnitTableProviderFactory implements TableProviderFactory {
    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory is null");
        }
        if (dir.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        return new JUnitTableProvider(dir);
    }
}
