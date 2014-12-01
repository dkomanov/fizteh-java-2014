package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
import java.util.HashMap;

public class FileMapAction implements Container {
    private FileBase data;
    private HashMap<String[], Command> hash;

    public FileMapAction(FileBase data) {
        hash = new HashMap<String[], Command>();
        this.data = data;
    }

    @Override
    public Command getCommandByName(String s) throws ExceptionUnknownCommand {
        String[] array = s.trim().split("\\s+");
        if (hash.containsKey(array)) {
            return hash.get(array);
        }
        Command result = null;
        if (array[0].equals("get")) {
            result = new Get(array, data);
        }
        if (array[0].equals("remove")) {
            result = new Remove(array, data);
        }
        if (array[0].equals("list")) {
            result = new List(array, data);
        }
        if (array[0].equals("exit")) {
            result = new Exit(array, data);
        }
        if (array[0].equals("put")) {
            result = new Put(array, data);
        }
        if (result == null) {
                throw new ExceptionUnknownCommand();
        }
        hash.put(array, result);
        return result;
    }
}
