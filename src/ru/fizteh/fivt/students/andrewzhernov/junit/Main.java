package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        try {
            String initialDirectory = System.getProperty("fizteh.db.dir");
            TableProvider database = new TableProvider(initialDirectory);
            Shell shell = new Shell(database, new Command[] {
                new Command("size", 1, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().size();
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.out.println((Integer)returnValue);
                    }
                }),
                new Command("put", 3, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().put(args[1], args[2]);
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        String value = (String)returnValue;
                        if (value == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(value);
                        }
                    }
                }),
                new Command("get", 2, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().get(args[1]);
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        String value = (String)returnValue;
                        if (value == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("found");
                            System.out.println(value);
                        }
                    }
                }),
                new Command("remove", 2, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().remove(args[1]);
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        if ((String)returnValue == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("removed");
                        }
                    }
                }),
                new Command("list", 1, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().list();
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>)returnValue;
                        System.out.println(String.join(", ", list));
                    }
                }),
                new Command("commit", 1, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().commit();
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.out.println((Integer)returnValue);
                    }
                }),
                new Command("rollback", 1, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().rollback();
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.out.println((Integer)returnValue);
                    }
                }),
                new Command("create", 2, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.createTable(args[1]);
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.out.println("created");
                    }
                }),
                new Command("drop", 2, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        database.removeTable(args[1]);
                        return null;
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.out.println("dropped");
                    }
                }),
                new Command("use", 2, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.useTable(args[1]);
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.out.println("using tablename");
                    }
                }),
                new Command("show tables", 2, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        return database.showTables();
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        @SuppressWarnings("unchecked")
                        Map<String, Integer> tables = (Map<String, Integer>)returnValue;
                        for (String tablename : tables.keySet()) {
                            System.out.printf("%s %d\n", tablename, tables.get(tablename));
                        }
                    }
                }),
                new Command("exit", 1, new AbstractHandler() {
                    @Override
                    public Object exec(TableProvider database, String[] args) throws Exception {
                        database.exit();
                        return null;
                    }
                    @Override
                    public void print(Object returnValue) throws Exception {
                        System.exit(0);
                    }
                })
            });
            shell.run(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
