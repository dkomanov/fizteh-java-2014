package ru.fizteh.fivt.students.kinanAlsarmini.storable.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;

public class DatabaseProviderTests {
    TableProviderFactory factory = new DatabaseTableProviderFactory();

    @Test(expected = IOException.class)
    public void createProviderUnavailableShouldFail() throws IOException {
        factory.create("F:\\");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderEmptyShouldFail() {
        try {
            factory.create("");
        } catch (IOException e) {

        }
    }
}
