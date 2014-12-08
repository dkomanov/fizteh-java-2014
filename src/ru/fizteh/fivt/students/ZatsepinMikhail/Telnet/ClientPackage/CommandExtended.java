package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Command;

public abstract class CommandExtended<T> extends Command<T> {
    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public String getName() {
        return name;
    }
}
