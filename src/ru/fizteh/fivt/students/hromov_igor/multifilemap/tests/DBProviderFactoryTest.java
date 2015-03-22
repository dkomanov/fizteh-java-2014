package ru.fizteh.fivt.students.hromov_igor.multifilemap.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProviderFactory;

public class DBProviderFactoryTest {

    private TableProviderFactory newFactory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void before() {
        newFactory = new DBProviderFactory();
    }


    @Test(expected = Exception.class)
    public void createWithNullArgs() throws Exception {
        newFactory.create(null);
    }
}
