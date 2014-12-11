package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.students.deserg.telnet.Shell;
import ru.fizteh.fivt.students.deserg.telnet.exceptions.MyException;

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

    public ClientAgent(Socket socket, DbTableProvider db) {

        this.socket = socket;
        this.db = db;

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
                    result = Shell.executeServerCommand(inputCommand, db);
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
