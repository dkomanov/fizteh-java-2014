package multi_file_map.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class TableHandler {
  private File path;
  private HashMap<String, BinaryDirHandler> binaryDirHandlers;

  public TableHandler(File _path) throws IOException {
    path = _path;
    binaryDirHandlers = new HashMap<String, BinaryDirHandler>();
    if (!path.isDirectory()) {
      throw new IOException("Table is not directory");
    }
    if (path.listFiles() != null) {
      for (File inFile : path.listFiles()) {
        binaryDirHandlers.put(inFile.getName(), new BinaryDirHandler(inFile));  
      }
    }
  }

  public String work(String[] tokens) throws IOException {
    if (tokens.length == 1 && tokens[0].equals("list")) {
      String resString = "";
      Set<String> res = new TreeSet<String>();
      for (BinaryDirHandler cur : binaryDirHandlers.values()) {
        res.addAll(cur.get_list());
      }
      String[] keys = res.toArray(new String[res.size()]);

      if (keys.length == 0) {
        return resString;
      }
      resString += keys[0];
      for (int i = 1; i < keys.length; i++) {
        resString += ", " + keys[i];
      }
      return resString;
    } else if (tokens.length >= 2) {
      String key = tokens[1];
      int dir = key.hashCode() % 16;
      String dirStr = Integer.toString(dir) + ".dir";
      File dirFile = new File(path, dirStr);
      if (!binaryDirHandlers.containsKey(dirStr)) {
        dirFile.mkdirs();
        binaryDirHandlers.put(dirStr, new BinaryDirHandler(dirFile));  
      }
      String res = binaryDirHandlers.get(dirStr).work(tokens);
      if (binaryDirHandlers.get(dirStr).get_count() == 0) {
        for (File file : dirFile.listFiles()) {
          file.delete();
        }
        dirFile.delete();
        binaryDirHandlers.remove(dirStr);
      }
      return res;
    } 
    throw new IOException("Incorrect command");
  }

  public int get_count() {
    int res = 0;
    for (BinaryDirHandler cur : binaryDirHandlers.values()) {
      res += cur.get_count();
    }
    return res;
  }

}
