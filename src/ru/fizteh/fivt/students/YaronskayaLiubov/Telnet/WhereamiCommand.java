package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
public class WhereamiCommand extends TelnetCommand {
    public WhereamiCommand() {
        name = "whereami";
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (tableProvider.isLocal()) {
            return "local";
        } else {
            return "remote " + tableProvider.getHost() + " " + tableProvider.getPort();
        }
    }
}
