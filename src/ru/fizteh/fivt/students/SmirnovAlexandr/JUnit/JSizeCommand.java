package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JSizeCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().size());
        }
    }
    protected final int arg = 0;

    protected int getArg() {
        return arg;
    }
}
