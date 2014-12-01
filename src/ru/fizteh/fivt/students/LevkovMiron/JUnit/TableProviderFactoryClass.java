package ru.fizteh.fivt.students.LevkovMiron.JUnit;

/**
 * Created by Мирон on 24.10.2014 ru.fizteh.fivt.students.LevkovMiron.JUnit.
 */
public class TableProviderFactoryClass implements TableProviderFactory {
    @Override
    public TableProviderClass create(String dir) {
        try {
            return new TableProviderClass(dir);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
