package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public class FileMapAction implements CommandFromString {
    private FileBase data;

    public FileMapAction(FileBase data) {
        this.data = data;
    }

    @Override
    public Command fromString(String s) throws UnknownCommand {
        final String[] mas = s.trim().split("\\s+");

        switch (mas[0]) {
            case "get":
                return new Command() {
                    @Override
                    public void execute() {
                           if (mas.length != 2) {
                               System.err.println("get have 1 parameters");
                               System.exit(1);
                           }
                          String value = data.m.get(mas[1]);
                          if (value == null) {
                              System.out.println("not found");
                          } else {
                              System.out.println(value);
                          }
                    }

                    @Override
                    public String name() {
                        return "get";
                    }
                };

            case "remove":
                return new Command() {
                    @Override
                    public void execute() {
                        if (mas.length != 2) {
                            System.err.println("remove have 1 parameters");
                            System.exit(1);
                        }
                        if (data.m.remove(mas[1]) == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("removed");
                        }
                    }

                    @Override
                    public String name() {
                        return "remove";
                    }
                };

            case "list":
                return new Command() {
                    @Override
                    public void execute() {
                        if (mas.length != 1) {
                            System.err.println("list haven't any parameters");
                            System.exit(1);
                        }
                        for (String k : data.m.keySet()) {
                            System.out.println(k);
                        }
                    }

                    @Override
                    public String name() {
                        return "list";
                    }
            };

            case "exit":
                return new Command() {
                    @Override
                    public void execute() throws StopProcess {
                        if (mas.length != 1) {
                            System.err.println("exit haven't any parameters");
                            System.exit(1);
                        }
                        throw new StopProcess();
                    }
                    @Override
                    public String name() {
                        return "exit";
                    }
                };
            case "put":
                return new Command() {
                    @Override
                    public String name() {
                        return "put";
                    }

                    @Override
                    public void execute() throws StopProcess {
                        if (mas.length != 3) {
                            System.err.println("put have 2 parameters");
                            System.exit(1);
                        }
                        String s = data.m.put(mas[1], mas[2]);
                        if (s == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(s);
                        }
                    }
                };
            default:
                throw new UnknownCommand();
        }
    }
}
