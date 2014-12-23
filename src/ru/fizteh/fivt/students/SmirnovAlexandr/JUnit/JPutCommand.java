package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

public class JPutCommand extends JCommand {

    String key;
    String value;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            String result = base.getUsing().put(key, value);
            if (result == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println("result");
            }
        }
    }

    protected final int arg =  2;

    protected int getArg() {
        return arg;
    }

    @Override
    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }
}
