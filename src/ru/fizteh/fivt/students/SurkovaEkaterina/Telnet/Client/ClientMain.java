package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

public class ClientMain {
    public static void main(String[] args) {
        TelnetClient client = new TelnetClient();
        ClientShell shell = new ClientShell(client);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (client.works()) {
                    try {
                        client.disconnect();
                    } catch (Exception e) {
                        System.out.println("Cannot break the connection!");
                    }
                }
            }
        });

        shell.setArguments(args);
        shell.beginExecuting();
    }
}


