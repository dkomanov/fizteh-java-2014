package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JCreateCommand extends JCommand {
    private String tableName;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.createTable(tableName) == null) {
            System.out.println(tableName + " exists");
        } else {
            System.out.println("created");
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
