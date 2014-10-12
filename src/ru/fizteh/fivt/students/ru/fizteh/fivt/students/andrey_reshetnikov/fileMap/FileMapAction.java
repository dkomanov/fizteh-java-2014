package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public class FileMapAction implements CommandContainer {
    private FileBase data;

    public FileMapAction(FileBase data) {
        this.data = data;
    }

    @Override
    public Command getCommandByName(String s) throws UnknownCommand {
        final String[] array = s.trim().split("\\s+");

        switch (array[0]) {
            case "get":
                return new Command() {
                    @Override
                    public void execute() {
                           if (array.length != 2) {
                               System.err.println("get accepts 1 parameter");
                               System.exit(1);
                           }
                          String value = data.m.get(array[1]);
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
                        if (array.length != 2) {
                            System.err.println("remove accepts 1 parameters");
                            System.exit(1);
                        }
                        if (data.m.remove(array[1]) == null) {
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
                        if (array.length != 1) {
                            System.err.println("list haven't any parameters");
                            System.exit(1);
                        }
                        StringBuilder allkeys = new StringBuilder();
                        for (String k : data.m.keySet()) {
                            if (allkeys.length() > 0) {
                                allkeys.append(", ");
                            }
                            allkeys.append(k);
                        }
                        System.out.println(allkeys.toString());
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
                        if (array.length != 1) {
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
                        if (array.length != 3) {
                            System.err.println("put accepts 2 parameters");
                            System.exit(1);
                        }
                        String s = data.m.put(array[1], array[2]);
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
