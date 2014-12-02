package ru.fizteh.fivt.students.SukhanovZhenya.Junit;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class ProviderFactory implements TableProviderFactory {
    @Override
    public Provider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Null argument");
        }

        return new Provider(dir);
    }
}
