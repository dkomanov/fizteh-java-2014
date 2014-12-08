package multi_file_map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import multi_file_map.util.DatabaseHandler;

class MultiFileMap {
  private static void workWithLine(DatabaseHandler handler, String s) throws IOException {
    String[] stringPieces = s.split(";");
    for (String piece : stringPieces) {
      List<String> items = new ArrayList<String>(Arrays.asList(piece.split(" ")));
      items.removeAll(Arrays.asList(""));
      String[] tokens = items.toArray(new String[items.size()]);

      System.out.println(handler.work(tokens));
    }
  }

  public static void main(String[] args) {
    String workDir = System.getProperty("fizteh.db.dir");
    if (workDir == null) {
      System.out.println("Incorrect working direcroty");
      System.exit(1);
    }
    try {
      DatabaseHandler handler = new DatabaseHandler(new File(workDir));

      if (args.length == 0) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
          String s = sc.nextLine();
          workWithLine(handler, s);
        }
        sc.close();
      } else {
        workWithLine(handler, args[0]);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
  }
}
