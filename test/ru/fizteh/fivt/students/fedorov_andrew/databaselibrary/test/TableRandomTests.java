package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestActions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.ProbableActionSet;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestAction;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

@RunWith(Parameterized.class)
public class TableRandomTests extends TestBase {

    @Parameters
    public static Iterable<Object[]> data() {
	int testsCount = TestUtils.randInt(100, 1000);

	List<Object[]> testSuits = new LinkedList<>();
	ProbableActionSet<TestAction> generator = new ProbableActionSet<>();
	generator.add(Commit, 1).add(GetExistent, 7).add(GetNotExistent, 2)
		.add(GlobalCheck, 8).add(List, 5).add(PutNew, 9)
		.add(PutOverwrite, 3).add(RemoveExistent, 4)
		.add(RemoveNotExistent, 2).add(Rollback, 1).add(Size, 4);

	for (int testSuit = 0; testSuit < testsCount; testSuit++) {
	    int actionsCount = TestUtils.randInt(10, 10 * (testSuit + 1));
	    List<TestAction> actions = new LinkedList<TestAction>();

	    for (int i = 0; i < actionsCount; i++) {
		actions.add(generator.nextAction());
	    }
	    actions.add(GlobalCheck);
	    testSuits.add(new Object[] { actions });
	}

	return testSuits;
    }

    public static TableProviderFactory factory;

    @Parameter
    public List<TestAction> actions;

    private TableProvider provider;
    private Table table;

    private Map<String, String> lastCommit = new HashMap<String, String>();
    private Map<String, String> backMap = new HashMap<String, String>();

    @BeforeClass
    public static void globalPrepare() {
	factory = TestUtils.obtainFactory();
    }

    @Before
    public void prepare() {
	provider = factory.create(dbRoot.toString());
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
    public void test() {
	for (TestAction action : actions) {
	    action.perform(table, backMap, lastCommit);
	}
    }
}
