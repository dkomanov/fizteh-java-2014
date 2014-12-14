package filemap.util;

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

public class BinaryFileHandler {

  private String fileRoute;

  public BinaryFileHandler(String getFileRoute) {
    fileRoute = getFileRoute;
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
    byte[] b = s.getBytes("UTF-8");
    out.writeInt(b.length);
    for (int i = 0; i < b.length; i++) {
      out.writeByte(b[i]);
    }
  }

  public Map<String, String> sync() throws IOException {
    File base = new File(fileRoute);
    if (!base.exists()) {
      base.createNewFile();
    }
    try (BufferedInputStream bufferedStream = new BufferedInputStream(
        new FileInputStream(base));
        DataInputStream dataStream = new DataInputStream(bufferedStream)) {
      Map<String, String> map = new HashMap<String, String>();
      while (bufferedStream.available() > 0) {
        int lenKey = dataStream.readInt();
        String stringKey = readString(lenKey, dataStream);
        int lenVal = dataStream.readInt();
        String stringVal = readString(lenVal, dataStream);
        map.put(stringKey, stringVal);
      }
      return map;
    }
  }

  public void syncFrom(Map<String, String> map) throws IOException {
    try (BufferedOutputStream bufferedStream = new BufferedOutputStream(
        new FileOutputStream(fileRoute));
        DataOutputStream dataStream = new DataOutputStream(bufferedStream)) {
      for (Map.Entry<String, String> entry : map.entrySet()) {
        writeString(entry.getKey(), dataStream);
        writeString(entry.getValue(), dataStream);
      }
    }
  }

}
