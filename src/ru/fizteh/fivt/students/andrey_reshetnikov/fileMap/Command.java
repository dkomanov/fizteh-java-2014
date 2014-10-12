package ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public abstract class Command {
    protected String[] args;
    protected FileBase data;

    abstract String name();
    abstract void execute() throws StopProcess, IncorrectInputException;
    protected Command(){}

    public static class GetCommand extends Command {
        public GetCommand(String[] a, FileBase data) {
            args = a;
            this.data = data;
        }
        @Override
        public void execute() throws IncorrectInputException {
            if (args.length != 2) {
                System.out.println("get accepts 1 parameter");
                System.out.flush();
                throw new IncorrectInputException();
            }
            String value = data.m.get(args[1]);
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
    }

    public static class RemoveCommand extends Command {
        public RemoveCommand(String[] a, FileBase data) {
            args = a;
            this.data = data;
        }
        @Override
        public void execute() throws IncorrectInputException {
            if (args.length != 2) {
                System.out.println("remove accepts 1 parameters");
                System.out.flush();
                throw new IncorrectInputException();
            }
            if (data.m.remove(args[1]) == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
        @Override
        public String name() {
            return "remove";
        }
    }

    public static class ListCommand extends Command {
        public ListCommand(String[] a, FileBase data) {
            args = a;
            this.data = data;
        }
        @Override
        public void execute() throws IncorrectInputException {
            if (args.length != 1) {
                System.out.println("list haven't any parameters");
                System.out.flush();
                throw new IncorrectInputException();
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
    }

    public static class ExitCommand extends Command {
        public ExitCommand(String[] a, FileBase data) {
            args = a;
            this.data = data;
        }
        @Override
        public void execute() throws StopProcess, IncorrectInputException {
            if (args.length != 1) {
                System.out.println("exit haven't any parameters");
                System.out.flush();
                throw new IncorrectInputException();
            }
            throw new StopProcess();
        }
        @Override
        public String name() {
            return "exit";
        }
    }

    public static class PutCommand extends Command {
        public PutCommand(String[] a, FileBase data) {
            args = a;
            this.data = data;
        }
        @Override
        public void execute() throws IncorrectInputException {
            if (args.length != 3) {
                System.out.println("put accepts 2 parameters");
                System.out.flush();
                throw new IncorrectInputException();
            }
            String s = data.m.put(args[1], args[2]);
            if (s == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(s);
            }
        }
        @Override
        public String name() {
            return "put";
        }
    }
}
