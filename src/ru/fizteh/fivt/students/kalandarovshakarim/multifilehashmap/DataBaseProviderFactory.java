/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap;

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
        ShellUtils shell = new ShellUtils();
        try {
            shell.chDir(dir);
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("DataBase dir is not specified");
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException(dir + " No such Directory");
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        DataBaseProvider retVal = null;
        try {
            retVal = new DataBaseProvider(dir);
        } catch (IOException ex) {
            //nothing
        }
        return retVal;
    }
}
