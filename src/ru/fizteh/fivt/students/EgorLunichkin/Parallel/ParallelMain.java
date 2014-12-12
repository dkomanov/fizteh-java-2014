package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ParallelMain {
    public static void main(String[] args) {
        try {
            new Executor(args);
        } catch (ExitException ex) {
            if (ex.getCode() == 0) {
                return;
            }
            System.err.println(ex.getMessage());
            System.exit(ex.getCode());
        }
    }
}
