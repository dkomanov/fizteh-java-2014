import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.Table;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProvider;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProviderFactoryClass;

import java.util.Collections;
import java.util.List;

/**
 * Created by Мирон on 27.10.2014 PACKAGE_NAME.
 */
public class JUnitTest {
    @Test
    public void testTableProviderFactory(){
        Assert.assertNotNull(new TableProviderFactoryClass().create("C:/"));
        Assert.assertNull(new TableProviderFactoryClass().create("C:/Test/1.txt"));
        Assert.assertNull(new TableProviderFactoryClass().create(null));
    }



    @Test
    public void testTableProviderCreate(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNotNull(provider.createTable("table2"));
        provider.removeTable("table1");
        provider.removeTable("table2");
    }
    @Test
    public void testTableProviderGet(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        Assert.assertNull(provider.getTable("table2"));
        provider.createTable("table1");
        Assert.assertNotNull(provider.getTable("table1"));
        provider.removeTable("table1");
    }
    @Test
    public void testTableProviderRemove(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        provider.removeTable("table1");
        Assert.assertNull(provider.getTable("table1"));
    }



    @Test
    public void testTableCommit(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        table.commit();
        table = provider.getTable("table1");
        Assert.assertEquals(table.size(),1);
        Assert.assertEquals(table.get("1"), "2");
        provider.removeTable("table1");
    }
    @Test
    public void testTableRollback(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table2");
        Table table = provider.getTable("table2");
        table.put("1", "2");
        Assert.assertEquals(table.size(), 1);
        table.commit();
        table.put("2", "3");
        table.put("2", "4");
        Assert.assertEquals(table.size(), 2);
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.get("1"), "2");
        table.remove("1");
        table.rollback();
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.get("1"), "2");
        provider.removeTable("table2");
    }
    @Test
    public void testTableGetName(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        Assert.assertEquals(table.getName(), "Test");
        provider.removeTable("table1");
    }
    @Test
    public void testTableGet(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        Assert.assertEquals(table.get("1"), "2");
        Assert.assertNull(table.get("2"));
        provider.removeTable("table1");
    }
    @Test
    public void testTablePut(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        Assert.assertNull(table.put("1", "2"));
        Assert.assertEquals(table.put("1", "3"), "2");
        provider.removeTable("table1");
    }
    @Test
    public void testTableSize(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        Assert.assertEquals(table.size(),0);
        table.put("1", "2");
        Assert.assertEquals(table.size(), 1);
        table.put("2", "3");
        Assert.assertEquals(table.size(), 2);
        table.remove("1");
        Assert.assertEquals(table.size(), 1);
        table.put("2", "3");
        Assert.assertEquals(table.size(), 1);
        provider.removeTable("table1");
    }
    @Test
    public void testTableRemove(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        Assert.assertNull(table.remove("a"));
        Assert.assertEquals(table.remove("1"), "2");
        Assert.assertEquals(table.size(), 0);
        provider.removeTable("table1");
    }
    @Test
    public void testTableList(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        table.put("2", "3");
        table.put("1", "3");
        List<String> list = table.list();
        Collections.sort(list);
        String[] arrayList = new String[2];
        arrayList[0] = list.get(0);
        arrayList[1] = list.get(1);
        Assert.assertEquals(list.size(),2);
        String[] testList = new String[2];
        testList[0] = "1";
        testList[1] = "2";
        Assert.assertArrayEquals(testList, arrayList);
        provider.removeTable("table1");
    }
}
