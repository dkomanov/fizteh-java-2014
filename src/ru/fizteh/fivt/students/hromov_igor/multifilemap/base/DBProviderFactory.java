package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DBProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws Exception{
        try{
            if (dir == null) {
                throw new Exception();
            }
        } catch (Exception e){
            System.err.println("Null Factory");
        }
        return new DBProvider(dir);
    }
}