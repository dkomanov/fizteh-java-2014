import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import util.BinaryFileHandler;

class FileMap {
  public static String work(Map<String, String> map, String s) throws IOException {
    String[] parts = s.split(" ");
    if (parts.length == 3 && parts[0].equals("put") ) {
      if (map.containsKey(parts[1])) {
        String oldVal = map.get(parts[1]);
        map.put(parts[1], parts[2]);
        return "overwrite\n" + oldVal;
      } else {
        map.put(parts[1], parts[2]);
        return "new";
      }
    }
    if (parts.length == 2) {
      if (parts[0].equals("get")) {
        if (map.containsKey(parts[1])) {
          return "found\n" + map.get(parts[1]);
        }
        else {
          return "not found";
        }
      }
      if (parts[0].equals("remove")) {
        if (map.containsKey(parts[1])) {
          map.remove(parts[1]);
          return "removed";
        } else {
          return "not found";
        }
      }
    }
    if (parts.length == 1) {
      if (parts[0].equals("list")) {
        String res = "";
        Set<String> mapKeys = map.keySet();
        String[] keys = mapKeys.toArray(new String[mapKeys.size()]);
        if (keys.length == 0) {
          return res;
        }
        res += keys[0];
        for (int i=1;i<keys.length;i++) {
          res += ", " + keys[i];
        }
        return res;
      }
      if (parts[0].equals("exit")) {
        System.exit(0);
      }
    }
    throw new IOException();
  }

  public static void main(String[] args) {
    String fileRoute = System.getProperty("db.file");
    if (fileRoute == null) {
      System.out.println("Incorrect db.file");
      System.exit(1);
    }
    BinaryFileHandler binaryHandler = new BinaryFileHandler(fileRoute);
    try {
      Map<String, String> map = binaryHandler.sync();
      if (args.length == 0) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
          String s = sc.nextLine();
          try {
            System.out.println( work(map, s) );
          } catch (IOException e) {
            System.out.println("Incorrect command");
            System.exit(1);
          }
          binaryHandler.sync_from(map);
        }
        sc.close();
      } else {
        try {
          System.out.println( work(map, args[0]) );
        } catch (IOException e) {
          System.out.println("Incorrect command");
          System.exit(1);
        }
        binaryHandler.sync_from(map);
      }
    } catch (IOException e) {
      System.out.println("Something wrong with files");
    }
  }
}
