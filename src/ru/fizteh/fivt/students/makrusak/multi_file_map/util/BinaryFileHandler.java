package multi_file_map.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BinaryFileHandler {

  private File path;
  private Map<String, String> map;

  public BinaryFileHandler(File _path) throws IOException{
    path = _path;
    if (!path.isFile()) {
      throw new IOException("Binary file is not file.");
    }

    String[] parts = path.getName().split("\\.");
    try {
      int num = Integer.parseInt(parts[0]);
      if (parts.length != 2 || !parts[1].equals("dat") || num < 0 || num >= 16) {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new IOException("Incorrect file name for binary file.");
    }
    sync();
  }

  private String readString(int bytes, DataInputStream in) throws IOException {
    byte[] b = new byte[bytes];
    for (int i = 0; i < bytes; i++) {
      int c = in.readByte();
      b[i] = (byte) c;
    }
    return new String(b, "UTF-8");
  }

  private void writeString(String s, DataOutputStream out) throws IOException {
    byte b[] = s.getBytes("UTF-8");
    out.writeInt(b.length);
    for (int i = 0; i < b.length; i++) {
      out.writeByte(b[i]);
    }
  }

  private void sync() throws IOException {
    try (BufferedInputStream bufferedStream = new BufferedInputStream(
        new FileInputStream(path));
        DataInputStream dataStream = new DataInputStream(bufferedStream)) {
      map = new HashMap<String, String>();
      while (bufferedStream.available() > 0) {
        int lenKey = dataStream.readInt();
        String stringKey = readString(lenKey, dataStream);
        int lenVal = dataStream.readInt();
        String stringVal = readString(lenVal, dataStream);
        map.put(stringKey, stringVal);
      }
    }
  }

  private void back_sync() throws IOException {
    try (BufferedOutputStream bufferedStream = new BufferedOutputStream(
        new FileOutputStream(path));
        DataOutputStream dataStream = new DataOutputStream(
            bufferedStream)) {
      for (Map.Entry<String, String> entry : map.entrySet()) {
        writeString(entry.getKey(), dataStream);
        writeString(entry.getValue(), dataStream);
      }
    }
  }

  public String work(String[] parts) throws IOException {
    if (parts.length == 3 && parts[0].equals("put")) {
      if (map.containsKey(parts[1])) {
        String oldVal = map.get(parts[1]);
        map.put(parts[1], parts[2]);
        back_sync();
        return "overwrite\n" + oldVal;
      } else {
        map.put(parts[1], parts[2]);
        back_sync();
        return "new";
      }
    }
    if (parts.length == 2) {
      if (parts[0].equals("get")) {
        if (map.containsKey(parts[1])) {
          return "found\n" + map.get(parts[1]);
        } else {
          return "not found";
        }
      }
      if (parts[0].equals("remove")) {
        if (map.containsKey(parts[1])) {
          map.remove(parts[1]);
          back_sync();

          return "removed";
        } else {
          return "not found";
        }
      }      
    }
    throw new IOException("Incorrect command");
  }

  public Set<String> get_list() {
    return map.keySet();
  }

  public int get_count() {
    return map.keySet().size();
  }
}
