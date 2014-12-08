package multi_file_map.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import multi_file_map.util.TableHandler;

public class DatabaseHandler {
  private File path;
  private HashMap<String, TableHandler> tableHandlers;
  private String current_table;

  public DatabaseHandler(File _path) throws IOException {
    path = _path;
    tableHandlers = new HashMap<String, TableHandler>();
    if (!path.exists()) {
      path.mkdirs();
    } else if (!path.isDirectory()) {
      throw new IOException("Incorrect path to directory.");
    }
    if (path.listFiles() != null) {
      for (File inFile : path.listFiles()) {
        tableHandlers.put(inFile.getName(), new TableHandler(inFile));
      }
    }
  }

  public String work(String[] tokens) throws IOException {
    if (tokens.length == 2 && tokens[0].equals("create")) {
      if (tableHandlers.containsKey(tokens[1])) {
        return tokens[1] + " exists";
      } else {
        File dir = new File(path, tokens[1]);
        dir.mkdirs();
        tableHandlers.put(dir.getName(), new TableHandler(dir));
        return "created";
      }
    } else if (tokens.length == 2 && tokens[0].equals("drop")) {
      if (!tableHandlers.containsKey(tokens[1])) {
        return tokens[1] + " not exists";
      } else {
        File dir = new File(path, tokens[1]);
        for (File binDir : dir.listFiles()) {
          for (File bin : binDir.listFiles()) {
            bin.delete();
          }
          binDir.delete();
        }
        dir.delete();
        tableHandlers.remove(tokens[1]);
        if (current_table != null && current_table.equals(tokens[1])) {
          current_table = null;
        }
        return "dropped";
      }
    } else if (tokens.length == 1 && tokens[0].equals("exit")) {
      System.exit(0);
    } else if (tokens.length == 2 && tokens[0].equals("use")) {
      if (!tableHandlers.containsKey(tokens[1])) {
        return tokens[1] + " not exists";
      } else {
        current_table = tokens[1];
        return "using " + current_table;
      }
    } else if (tokens.length == 2 && tokens[0].equals("show")
        && tokens[1].equals("tables")) {
      String res = "";
      for (Map.Entry<String, TableHandler> entry : tableHandlers.entrySet()) {
        res += entry.getKey() + " " + entry.getValue().get_count() + "\n";
      }
      return res;
    } else {
      if (current_table == null) {
        return "no table";
      } else {
        return tableHandlers.get(current_table).work(tokens);
      }
    }
    throw new IOException("Incorrect command");
  }
}
