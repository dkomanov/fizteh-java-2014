 package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

 /**
  * Created by Aliaksei Semchankau on 09.10.2014.
  */
 public class GetDatabase {

    /* Path pathDatabase;
     boolean interactive;
     HashMap<String, TableInfo> referenceToTableInfo;
     Queue<Vector<String>> listOfCommands;
     DatabaseFullInformation dbInfo;
     HashMap<String, TableInterface> tableCommandsHashMap;
     HashMap<String, CommandInterface> commandsHashMap;


     public static void main(String[] args) {
         GetDatabase processDatabase = new GetDatabase(args);
     }

     GetDatabase(String[] args) {

         pathDatabase = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
         dbInfo = new DatabaseFullInformation(pathDatabase);

         if (!Files.exists(pathDatabase)) {
             try {
                 Files.createDirectory(pathDatabase);
             } catch (IOException ioexc) {
                 throw new DatabaseException("can't create " + pathDatabase.toString());
             }
         }

         if (!Files.isDirectory(pathDatabase)) {
             throw new DatabaseException(pathDatabase + " isn't a direction");
         }

         DirectoryStream<Path> listOfDirs;

         try {
             listOfDirs = Files.newDirectoryStream(pathDatabase);
         } catch (IOException ioexc) {
             throw new DatabaseException(pathDatabase + ": can't make a list of directories");
         }

         referenceToTableInfo = new HashMap<String, TableInfo>();

         for (Path innerTable : listOfDirs) {

             if (!Files.isDirectory(innerTable)) {
                 throw new DatabaseException(innerTable + ": isn't a directiion");
             }
             String tableName = Difference.difference(pathDatabase.toString(), innerTable.toString());
             referenceToTableInfo.put(tableName, new TableInfo(innerTable, pathDatabase));
             referenceToTableInfo.get(tableName).openTable();
         }

         tableCommandsHashMap = new HashMap<String, TableInterface>();

         tableCommandsHashMap.put("create", new TableCreate());
         tableCommandsHashMap.put("drop", new TableDrop());
         tableCommandsHashMap.put("use", new TableUse());
         tableCommandsHashMap.put("show", new TableShowTables());

         commandsHashMap = new HashMap<String, CommandInterface>();

         commandsHashMap.put("get", new CommandGet());
         commandsHashMap.put("list", new CommandList());
         commandsHashMap.put("exit", new CommandExit());
         commandsHashMap.put("put", new CommandPut());
         commandsHashMap.put("remove", new CommandRemove());

         interactive = (args.length == 0);

         if (interactive) {
             while (true) {
                 readCommands(null);
                 doCommands();
                 if (dbInfo.exitFlag) {
                     break;
                 }
             }
         } else {
             readCommands(args);
             doCommands();
         }

         for (Map.Entry<String, TableInfo> entry : referenceToTableInfo.entrySet()) {
             entry.getValue().writeTable();
         }

     }

     public void readCommands(String[] args) {

         listOfCommands = new LinkedList<Vector<String>>();

         String toParse = "";
         if (args != null) {
             for (int i = 0; i < args.length; ++i) {
                 toParse += (args[i] + " ");
             }
         } else {
             Scanner line = new Scanner(System.in);
             toParse = line.nextLine();
         }

         if (toParse.length() == 0) {
             return;
         }

         String curArgument = "";

         for (int curSymbol = 0; ; ++curSymbol) {
             if (curSymbol != toParse.length() && toParse.charAt(curSymbol) != ';') {
                 curArgument += toParse.charAt(curSymbol);
             } else {

                 listOfCommands.add(processing(curArgument));

                 curArgument = "";
                 if (curSymbol == toParse.length()) {
                     break;
                 }
             }
         }

        /*while (!listOfCommands.isEmpty())
         {
             Vector<String> curLine = listOfCommands.poll();
             for (int i = 0; i < curLine.size(); ++i)
                 System.out.print(curLine.get(i) + " ");
             System.out.println(";");
         }*/
    /* }

     Vector<String> processing(String argLine) {
         Vector<String> argumentList = new Vector<String>();
         String argument = new String();
         argument = "";
         for (int symbol = 0; ; ++symbol) {
             if (symbol != argLine.length() && argLine.charAt(symbol) != ' ') {
                 argument += argLine.charAt(symbol);
             } else {
                 if (argument.length() > 0) {
                     argumentList.add(argument);
                     argument = "";
                 }

                 if (symbol == argLine.length()) {
                     break;
                 }
             }
         }

         return argumentList;
     }

     public void doCommands() {

         while (!listOfCommands.isEmpty()) {
             Vector<String> args = listOfCommands.poll();
             if (tableCommandsHashMap.get(args.elementAt(0)) != null) {
                 tableCommandsHashMap.get(args.elementAt(0)).makeCommand(args, referenceToTableInfo, dbInfo);
             } else if (commandsHashMap.get(args.elementAt(0)) != null) {
                 if (referenceToTableInfo.get(dbInfo.currentTableName) == null && !args.elementAt(0).equals("exit")) {
                     System.out.println("choose a table at first");
                 } else {
                     commandsHashMap.get(args.elementAt(0)).makeCommand(args, referenceToTableInfo, dbInfo);
                 }
             } else {
                 System.out.println("there is no such command");
             }
         }
     }*/

 }

