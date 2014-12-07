package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseTableProviderFactory;

import java.io.IOException;

public class TableProviderFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void createProviderNullDirectoryTest() {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            factory.create(null);
        } catch (IOException e) {
            System.out.println("Unexpected error! Something gone wrong!");
        }
    }
}
