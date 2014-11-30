package ru.fizteh.fivt.students.LevkovMiron.Storeable;

import java.nio.file.Paths;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        try {
            return new CTableProvider(Paths.get("").resolve(Paths.get(dir)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
