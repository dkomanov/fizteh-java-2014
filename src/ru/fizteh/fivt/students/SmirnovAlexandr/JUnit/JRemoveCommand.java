package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JRemoveCommand extends JCommand {

    String key;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            String result = base.getUsing().remove(key);
            if (result != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    protected final void putArguments(String[] args) {
        key = args[1];
    }

    protected final int arg = 1;

    protected int getArg() {
        return arg;
    }
}
