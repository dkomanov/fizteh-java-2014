package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class ObjectTableProviderFactory extends CommandTools implements TableProviderFactory {
    String dirName;
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        dirName = dir;
        try {
            if (dir == null || dir.length() > longestName) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        return new ObjectTableProvider(dir);
    }
}
