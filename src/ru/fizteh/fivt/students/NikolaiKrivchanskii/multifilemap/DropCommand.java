package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;


public class DropCommand implements Commands<MultiFileMapShellState> {
	public String getCommandName() {
		return "drop";
	}
	
	public int getArgumentQuantity() {
		return 1;
	}
	
	public void implement(String[] args, MultiFileMapShellState state) throws SomethingIsWrongException {
		try {
			state.tableProvider.removeTable(args[0]);
			System.out.println("removed");
		} catch (Exception e) {
			throw new SomethingIsWrongException (e.getMessage());
		}
	}

}