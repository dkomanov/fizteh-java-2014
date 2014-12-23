package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
abstract class TelnetCommand {
    String name;
    int numberOfArguments;

    @Override
    public String toString() {
        return name;
    }

    abstract String execute(RemoteDataTableProvider tableProvider, String[] args);

    public String serializeCmd(String[] args) {
        return String.join(" ", args);
    }
}
