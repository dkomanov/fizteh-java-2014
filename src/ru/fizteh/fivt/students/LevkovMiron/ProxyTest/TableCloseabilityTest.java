package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CTable;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CTableProvider;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CTableProviderFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Мирон on 01.12.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
@RunWith(Parameterized.class)
public class TableCloseabilityTest {

    private Method currentMethod;
    private CTableProvider provider;
    private CTable table;
    private CTableProviderFactory factory = new CTableProviderFactory();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        factory = new CTableProviderFactory();
        provider = (CTableProvider) factory.create(tmpFolder.newFolder().getAbsolutePath());
        table = (CTable) provider.createTable("t1", Arrays.asList(Integer.class, String.class));
    }

    @Parameterized.Parameters
    public static Collection<Method[]> init() {
        Method[] methods = CTable.class.getDeclaredMethods();
        Method[][] data = new Method[methods.length][1];
        for (int i = 0; i < methods.length; i++) {
            data[i][0] = methods[i];
        }
        return Arrays.asList(data);
    }

    public TableCloseabilityTest(Method method) {
        currentMethod = method;
    }

    @Test (expected = IllegalStateException.class)
    public void testCurrentMethod() throws Throwable {
        table.close();
        if (currentMethod.equals(CTable.class.getMethod("close")) || currentMethod.getModifiers() != 1) {
            throw new IllegalStateException();
        }
        Object[] args = new Object[currentMethod.getParameterCount()];
        if (currentMethod.getName().equals("getColumnType")) {
            args[0] = 0;
        }
        try {
            currentMethod.invoke(table, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
