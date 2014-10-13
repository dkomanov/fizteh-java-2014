/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        ShellUtils utils = new ShellUtils();
        TableProvider retVal = null;
        try {
            try {
                utils.mkDir(dir);
            } catch (IOException e) {
                // Если файл уже существует.
            }
            utils.chDir(dir);
            retVal = new DataBaseProvider(dir);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("DataBase dir is not specified");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(dir + " No such Directory");
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return retVal;
    }
}
