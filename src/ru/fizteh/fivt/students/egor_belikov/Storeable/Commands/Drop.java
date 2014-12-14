package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;

import java.io.IOException;

/**
 * Created by egor on 13.12.14.
 */
public class Drop implements Command {

    Boolean needCurrentTable = null;
    Integer numberOfArguments = null;

    public Drop() {
        needCurrentTable = false;
        numberOfArguments = 2;
    }
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        try {
            myTableProvider.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException ist) {
            System.out.println(args[1] + " does not exists");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
