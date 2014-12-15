package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JUseCommand extends JCommand {

    String tableName;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() != null && ((MyTable) base.getUsing()).unsavedChanges() != 0) {
            System.out.println(((MyTable) base.getUsing()).unsavedChanges() + " unsaved changes");
        } else {
            if (base.getTable(tableName) == null) {
                System.out.println(tableName + " not exists");
            } else {
                base.setUsing(tableName);
                System.out.println("using " + tableName);
            }
        }
    }
    protected int arg = 1;

    protected int getArg() {
        return arg;
    }

    @Override
    protected void putArguments(String[] args) {
        tableName = args[1];
    }
}
