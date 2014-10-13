/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        TableProvider retVal = null;
        try {
            Path dirPath = Paths.get(dir);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            retVal = new DataBaseProvider(dir);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("fizteh.db.dir is not specified");
        } catch (FileNotFoundException | NoSuchFileException e) {
            throw new IllegalArgumentException(dir + " No such Directory");
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return retVal;
    }
}
