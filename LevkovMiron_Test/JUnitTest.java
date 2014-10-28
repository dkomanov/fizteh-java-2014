import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.Table;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProvider;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProviderClass;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProviderFactoryClass;

/**
 * Created by Мирон on 27.10.2014 PACKAGE_NAME.
 */
public class JUnitTest {
    @Test
    public void TestTableProviderFactory(){
        Assert.assertNotNull(new TableProviderFactoryClass().create("C:/"));
        Assert.assertNull(new TableProviderFactoryClass().create("C:/Test/1.txt"));
        Assert.assertNull(new TableProviderFactoryClass().create(null));
    }



    @Test
    public void TestTableProvider_create(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNotNull(provider.createTable("table2"));
    }
    @Test
    public void TestTableProvider_get(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        Assert.assertNull(provider.getTable("table2"));
        Assert.assertNotNull(provider.getTable("table1"));
    }
    @Test
    public void TestTableProvider_remove(){
        TableProvider provider = new TableProviderFactoryClass().create("C:/Test");
        provider.createTable("table1");
        provider.removeTable("table1");
        Assert.assertNull(provider.getTable("table1"));
    }



    @Test
    public void TestTable_commit(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_rollback(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_getName(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
        Assert.assertEquals(table.getName(),"table1");
    }
    @Test
    public void TestTable_get(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
        table.put("1","2");
        Assert.assertEquals(table.get("1"),"2");
        Assert.assertNull(table.get("2"));
    }
    @Test
    public void TestTable_put(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_size(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_remove(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_list(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_exit(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_drop(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_loadTable(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_showTables(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_rewrite(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_rewriteFile(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_changesNumber(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_use(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_create(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }
    @Test
    public void TestTable_runCommand(){
        new TableProviderFactoryClass().create("C:/Test").createTable("table1");
        Table table = new TableProviderFactoryClass().create("C:/Test").getTable("table1");
    }

}
