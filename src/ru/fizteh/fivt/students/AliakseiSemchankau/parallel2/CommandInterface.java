package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;
import java.util.Vector;

public interface CommandInterface {

    void makeCommand(Vector<String> args, DatabaseProvider dProvider);

}
