package ru.fizteh.fivt.students.SukhanovZhenya.Storable;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class SProviderFactory implements TableProviderFactory {
    @Override
    public SProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Null argument");
        }

        return new SProvider(dir);
    }
}
