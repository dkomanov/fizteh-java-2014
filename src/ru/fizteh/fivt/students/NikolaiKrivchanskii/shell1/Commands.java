package ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1;


public interface Commands<State> {

    String getCommandName();
    
    int getArgumentQuantity();
    
    public void implement(String[] args, State state) throws SomethingIsWrongException;
}
