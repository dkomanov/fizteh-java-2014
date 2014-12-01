package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CTableProviderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Мирон on 01.12.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
@RunWith(Parameterized.class)
public class TableProviderFactoryCloseabilityTest {

    private Method currentMethod;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private CTableProviderFactory factory = new CTableProviderFactory();

    @Parameterized.Parameters
    public static Collection<Method[]> init() {
        Method[] methods = CTableProviderFactory.class.getDeclaredMethods();
        Method[][] data = new Method[methods.length][1];
        for (int i = 0; i < methods.length; i++) {
            data[i][0] = methods[i];
        }
        return Arrays.asList(data);
    }

    public TableProviderFactoryCloseabilityTest(Method method) {
        currentMethod = method;
    }

    @Test(expected = IllegalStateException.class)
    public void testCurrentMethod() throws Throwable {
        factory.close();
        if (currentMethod.getName().equals("close") || currentMethod.getModifiers() != 1) {
            throw new IllegalStateException();
        }
        Object[] args = new Object[currentMethod.getParameterCount()];
        try {
            currentMethod.invoke(factory, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
