package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JDropCommand extends JCommand {
    String tableName;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        try {
            base.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        }
    }

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    protected final int arg = 1;

    protected int getArg() {
        return arg;
    }
}
