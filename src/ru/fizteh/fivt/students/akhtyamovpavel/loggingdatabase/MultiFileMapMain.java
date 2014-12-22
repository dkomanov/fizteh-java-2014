package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user1 on 07.10.2014.
 */
public class MultiFileMapMain {
    public static void main(String[] args) {

        LoggerFactory loggerFactory = new LoggerFactory();
        HashMap map = new HashMap();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        Map proxy = (Map) loggerFactory.wrap(writer, map, Map.class);

        proxy.put("a", "a");
        proxy.put("5", "5");


        Shell shell = new Shell();
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        shell.setProvider(factory, Paths.get(System.getProperty("user.dir"))
                .resolve(System.getProperty("fizteh.db.dir")).toString());
        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            shell.startPacketMode(args);
        }
    }
}
