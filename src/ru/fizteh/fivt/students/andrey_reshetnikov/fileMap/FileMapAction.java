package ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;


import java.util.HashMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public class FileMapAction implements CommandContainer {
    private FileBase data;
    private HashMap<String[], Command> hash;

    public FileMapAction(FileBase data) {
        hash = new HashMap<String[], Command>();
        this.data = data;
    }

    @Override
    public Command getCommandByName(String s) throws UnknownCommand {
        String[] array = s.trim().split("\\s+");
        if (hash.containsKey(array)) {
            return hash.get(array);
        }
        Command res = null;
        switch (array[0]) {
            case "get":
                res = new Command.GetCommand(array, data);
                break;
            case "remove":
                res =  new Command.RemoveCommand(array, data);
                break;
            case "list":
                res =  new Command.ListCommand(array, data);
                break;
            case "exit":
                res =  new Command.ExitCommand(array, data);
                break;
            case "put":
                res =  new Command.PutCommand(array, data);
                break;
            default:
                throw new UnknownCommand();
        }
        hash.put(array, res);
        return res;
    }
}
