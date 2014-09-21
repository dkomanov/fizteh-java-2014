package ru.fizteh.fivt.students.ilivanov.shell;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandCat implements Command {
    private ArrayList<String> parameters;

    CommandCat(ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 2)
            throw new Exception("wrong number of parameters");
        else
            this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public int execute(){
        File file;
        try {
            Path path = Paths.get(Shell.currentDirectory.getCanonicalPath());
            path = path.resolve(parameters.get(1));
            file = new File(path.toAbsolutePath().toString());
            if (!file.exists()) {
                System.err.println("cat: \"" + parameters.get(1) + "\": no such file");
                return -1;
            }
        } catch (Exception e){
            System.err.println("cat: "+e.getMessage());
            return -1;
        }
        try( BufferedReader br = new BufferedReader(new FileReader(file)) ) {
            String line;
            while (true) {
                line = br.readLine();
                if (line != null)
                    System.out.println(line);
                else
                    break;
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }

        return 0;
    }
}
