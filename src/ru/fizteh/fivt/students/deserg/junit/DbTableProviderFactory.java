package ru.fizteh.fivt.students.deserg.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by deserg on 26.11.14.
 */
public class DbTableProviderFactory implements TableProviderFactory {

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */
    public TableProvider create(String dir) throws IllegalArgumentException {

        if (dir == null || dir.contains("\000")) {
            throw new IllegalArgumentException("Database \"" + dir + "\": invalid path");
        }

        Path path = Paths.get(System.getProperty("user.dir")).resolve(dir);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Database \"" + dir + "\": invalid path");
            }
        } else if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Database \"" + dir + "\": invalid path");
        }

        return new DbTableProvider(path);
    }

}
