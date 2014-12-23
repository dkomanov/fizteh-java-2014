package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ParallelMain {
    public static void main(String[] args) {
        try {
            Executor executor = new Executor(args);
            executor.run();
        } catch (ExitException ex) {
            if (ex.getCode() == 0) {
                return;
            }
            System.err.println(ex.getMessage());
            System.exit(ex.getCode());
        }
    }
}
