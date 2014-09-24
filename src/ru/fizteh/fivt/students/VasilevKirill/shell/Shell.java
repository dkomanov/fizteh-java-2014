package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasilev Kirill on 22.09.2014.
 */
public class Shell {
    static String currentPath = "C:";
    private static Map<String,Command> commandMap = new HashMap<>();

    static
    {
        commandMap.put(new CdCommand().toString(),new CdCommand());
        commandMap.put(new MkdirCommand().toString(),new MkdirCommand());
        commandMap.put(new PwdCommand().toString(),new PwdCommand());
        commandMap.put(new RmCommand().toString(), new RmCommand());
        commandMap.put(new CpCommand().toString(),new CpCommand());
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            new Shell().handle(System.in);
        } else {
            new Shell().handle(args);
        }
    }

    private void handle(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String command = "";
            while (!command.equals("exit")) {
                System.out.print("$ ");
                command = reader.readLine();
                String[] cmds = command.split("\\s+");
                Command currentCommand;
                if ((currentCommand = commandMap.get(cmds[0])) != null){
                        currentCommand.execute(cmds);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException caught");
        } catch (Exception e){
            System.err.println("Exception caught");
        }
    }

    private void handle(String[] args) {

    }
}
