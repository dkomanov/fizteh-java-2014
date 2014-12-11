package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.students.deserg.telnet.DbCommandExecuter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by deserg on 11.12.14.
 */
public class ClientAgent implements Callable<Integer> {

    Socket socket;
    DbTableProvider db;
    CommonData data;

    public ClientAgent(Socket socket, CommonData data) {

        this.socket = socket;
        this.data = data;
    }

    @Override
    public Integer call() throws Exception {

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        while (true) {

            String inputCommand = inputStream.readUTF();
            System.out.println(inputCommand);

            if (inputCommand.equals("disconnect") || inputCommand.equals("exit")) {

                db.close();
                socket.close();
                break;

            } else {
                String result;
                try {
                    result = DbCommandExecuter.executeDbCommand(inputCommand, db);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    throw ex;
                }
                outputStream.writeChars(result);
            }

        }

        return 0;
    }

}
