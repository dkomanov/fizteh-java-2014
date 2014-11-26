package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Data {
    public static HashMap<String, String> load(File fl) throws 
                                        IOException, FileNotFoundException {
        HashMap<String, String> hm = new HashMap<String, String>();
        DataInputStream dis = new DataInputStream(new FileInputStream(fl));

        while (dis.available() > 0) {
            int keyLen = dis.readInt();
            byte[] key = new byte[keyLen];
            for (int i = 0; i < keyLen; i++) {
                key[i] = dis.readByte();
            }
            int valLen = dis.readInt();
            byte[] val = new byte[valLen];
            for (int i = 0; i < valLen; i++) {
                val[i] = dis.readByte();
            }
            hm.put(new String(key, "UTF-8"), new String(val, "UTF-8"));
        }
        dis.close();
        return hm;
    }

    public static void save(File fl, HashMap<String, String> hm) throws 
                                        IOException, FileNotFoundException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fl));
        for (Map.Entry<String, String> element : hm.entrySet()) {
            byte[] b = element.getKey().getBytes("UTF-8");
            dos.writeInt(b.length);
            for (int i = 0; i < b.length; i++) {
                dos.writeByte(b[i]);
            }
            b = element.getValue().getBytes("UTF-8");
            dos.writeInt(b.length);
            for (int i = 0; i < b.length; i++) {
                dos.writeByte(b[i]);
            }
        }
        dos.close();
    }
}
