package ru.fizteh.fivt.students.LevkovMiron.Proxy;

import java.util.Arrays;

/**
 * Created by Мирон on 10.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        CStoreable storeable = new CStoreable(Arrays.asList(1, "2", "avacd", null, 1.5));
        Parser parser = new Parser();
        TableProviderFactory factory = new CTableProviderFactory();
        TableProvider provider = factory.create("C:/Test");
        Table table = provider.createTable("T1", Arrays.asList(Integer.class, String.class, String.class, Long.class, Double.class));
        Storeable myStoreable = parser.deserialize(table, "[1,2,avacd,null,1.5]");
        System.out.println(parser.serialize(table, myStoreable));
    }
}
