 package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.HashMap;
import java.util.Vector;

 /**
  * Created by Aliaksei Semchankau on 13.10.2014.
  */
 public class CommandPut implements CommandInterface {

     @Override
     public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

         if (args.size() != 3) {
             throw new DatabaseException("incorrect number of arguments(put)");
         }

         String key = args.elementAt(1);
         String newValue = args.elementAt(2);

         if (key == null || newValue == null) {
             throw new IllegalArgumentException("arguments for put can't be nulls");
         }

         if (dProvider.currentTableName == null) {
             System.out.println("choose a table at first");
             return;
         }
         Table dTable = dProvider.getTable(dProvider.currentTableName);

         if (dTable == null) {
             System.out.println("choose a table at first");
             return;
         }

         String oldValue = dTable.put(key, newValue);

        // System.out.println("old: " + oldValue);

         if (oldValue == null) {
             System.out.println("new");
         } else {
             System.out.println("overwrite");
             System.out.println(oldValue);
         }
     }

 }
