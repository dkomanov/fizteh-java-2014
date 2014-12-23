package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

public enum ClientState {
    LOCAL("local"),
    REMOTE_HOST_PORT("remote host port"),
    NOT_CONNECTED("not connected");

    private String name;

    public String toString() {
        return this.name;
    }

    ClientState(String name) {
        this.name = name;
    }
}
