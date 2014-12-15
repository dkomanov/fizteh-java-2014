package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JListCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(String.join(", ", base.getUsing().list()));
        }
    }
    protected final int arg = 0;

    protected int getArg() {
        return arg;
    }
}
