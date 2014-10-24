package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.ProbableActionSet;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestAction;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestActions.*;

@RunWith(Parameterized.class)
public class TableRandomTests extends TestBase {

    public static TableProviderFactory factory;
    @Parameter
    public List<TestAction> actions;
    private TableProvider provider;
    private Table table;
    private Map<String, String> lastCommit = new HashMap<String, String>();
    private Map<String, String> backMap = new HashMap<String, String>();

    @Parameters
    public static Iterable<Object[]> data() {
        int testsCount = TestUtils.randInt(100, 1000);

        List<Object[]> testSuits = new LinkedList<>();
        ProbableActionSet<TestAction> generator = new ProbableActionSet<>();
        generator.add(COMMIT, 1).add(GET_EXISTENT, 7).add(GET_NOT_EXISTENT, 2).add(GLOBAL_CHECK, 8)
                 .add(LIST, 5).add(PUT_NEW, 9).add(PUT_OVERWRITE, 3).add(REMOVE_EXISTENT, 4)
                 .add(REMOVE_NOT_EXISTENT, 2).add(ROLLBACK, 1).add(SIZE, 4);

        for (int testSuit = 0; testSuit < testsCount; testSuit++) {
            int actionsCount = TestUtils.randInt(10, 10 * (testSuit + 1));
            List<TestAction> actions = new LinkedList<TestAction>();

            for (int i = 0; i < actionsCount; i++) {
                actions.add(generator.nextAction());
            }
            actions.add(GLOBAL_CHECK);
            testSuits.add(new Object[] {actions});
        }

        return testSuits;
    }

    @BeforeClass
    public static void globalPrepare() {
        factory = TestUtils.obtainFactory();
    }

    @Before
    public void prepare() throws DatabaseException {
        provider = factory.create(DB_ROOT.toString());
        table = provider.createTable("table");
        lastCommit = new HashMap<String, String>();
        backMap = new HashMap<String, String>();
    }

    @After
    public void cleanup() throws IOException {
        provider = null;
        table = null;
        backMap.clear();
        backMap = null;
        lastCommit.clear();
        lastCommit = null;
        cleanDBRoot();
    }

    @Test
    public void test() throws DatabaseException {
        for (TestAction action : actions) {
            action.perform(table, backMap, lastCommit);
        }
    }
}
