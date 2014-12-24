package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

/**
 * Created by luba_yaronskaya on 19.12.14.
 */
public class StopCommand extends TelnetCommand {
    public StopCommand() {
        name = "stop";
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (args.length != 1) {
            throw new CommandRuntimeException(name + ": illegal number of arguments");
        }
        if (!tableProvider.isServer()) {
            throw new ServerRuntimeException("not started");
        }
        int port = tableProvider.getPort();
        tableProvider.stop();

        return "stopped at " + port;
    }
}
