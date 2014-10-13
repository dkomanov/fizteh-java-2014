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
            String key = "";
            for (int i = 0; i < keyLen; i++) {
                key += dis.readChar();
            }
            int valLen = dis.readInt();
            String val = "";
            for (int i = 0; i < valLen; i++) {
                val += dis.readChar();
            }
            hm.put(key, val);
        }
        dis.close();
        return hm;
    }

    public static void save(File fl, HashMap<String, String> hm) throws 
                                        IOException, FileNotFoundException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fl));
        for (Map.Entry<String, String> element : hm.entrySet()) {
            dos.writeInt(element.getKey().length());
            dos.writeChars(element.getKey());
            dos.writeInt(element.getValue().length());
            dos.writeChars(element.getValue());
        }
        dos.close();
    }
}
