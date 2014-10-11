package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.*;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.SomethingIsWrongException;

public interface TableProvider {
	Table getTable(String a) throws SomethingIsWrongException;
	Table createTable(String a) throws SomethingIsWrongException;
	void removeTable(String a) throws SomethingIsWrongException;
}