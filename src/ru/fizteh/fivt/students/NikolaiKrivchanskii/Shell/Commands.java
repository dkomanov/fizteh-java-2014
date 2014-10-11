package ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell;


public interface Commands<State> {

    public String getCommandName();
    
    public int getArgumentQuantity();
    
    abstract public void implement(String[] args, State state) throws SomethingIsWrongException;
}
