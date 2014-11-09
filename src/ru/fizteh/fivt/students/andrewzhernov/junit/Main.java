package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            TableProvider database = new TableProvider(System.getProperty("fizteh.db.dir"));
            Shell shell = new Shell(database, new Command[] {
                new Command("size", 1, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().size();
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        System.out.println((Integer) object);
                    }
                }),
                new Command("put", 3, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().put(args[1], args[2]);
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        String value = (String) object;
                        if (value == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(value);
                        }
                    }
                }),
                new Command("get", 2, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().get(args[1]);
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        String value = (String) object;
                        if (value == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("found");
                            System.out.println(value);
                        }
                    }
                }),
                new Command("remove", 2, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().remove(args[1]);
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        if ((String) object == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("removed");
                        }
                    }
                }),
                new Command("list", 1, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().list();
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>) object;
                        System.out.println(String.join(", ", list));
                    }
                }),
                new Command("commit", 1, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().commit();
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        System.out.println((Integer) object);
                    }
                }),
                new Command("rollback", 1, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.getCurrentTable().rollback();
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        System.out.println((Integer) object);
                    }
                }),
                new Command("create", 2, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.createTable(args[1]);
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        System.out.println("created");
                    }
                }),
                new Command("drop", 2, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        database.removeTable(args[1]);
                        return null;
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        System.out.println("dropped");
                    }
                }),
                new Command("use", 2, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.useTable(args[1]);
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        System.out.println("using tablename");
                    }
                }),
                new Command("show tables", 2, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        return database.showTables();
                    }
                    @Override
                    public void handle(Object object) throws Exception {
                        @SuppressWarnings("unchecked")
                        Map<String, Integer> tables = (Map<String, Integer>) object;
                        for (String tablename : tables.keySet()) {
                            System.out.printf("%s %d\n", tablename, tables.get(tablename));
                        }
                    }
                }),
                new Command("exit", 1, new HandlerInterface() {
                    @Override
                    public Object execute(TableProvider database, String[] args) throws Exception {
                        database.exit();
                        return null;
                    }
                    @Override
                    public void handle(Object object) throws Exception {
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
