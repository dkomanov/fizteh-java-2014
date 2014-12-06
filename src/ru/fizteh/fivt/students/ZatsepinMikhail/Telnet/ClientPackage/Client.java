package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        RealRemoteTableProvider provider = new RealRemoteTableProvider("localhost", 4000);
        List<Class<?>> typeList = new ArrayList<>();
        typeList.add(0, Integer.class);
        try {
            provider.createTable("simple", typeList);
            provider.close();
        } catch (IOException e) {
            //
        }

    }
}
