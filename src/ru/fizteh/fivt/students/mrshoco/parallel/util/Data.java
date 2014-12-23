package parallel.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Data {
    public static Map<String, String> load(File fl) throws IllegalArgumentException {
        try {
            Map<String, String> hm = new HashMap<String, String>();
            DataInputStream dis = new DataInputStream(new FileInputStream(fl));

            while (dis.available() > 0) {
                int keyLen = dis.readInt();
                if (keyLen <= 0 || keyLen > 100000) {
                    dis.close();
                    throw new IllegalArgumentException("Strange key length");
                }
                byte[] key = new byte[keyLen];
                for (int i = 0; i < keyLen; i++) {
                    key[i] = dis.readByte();
                }
                int valLen = dis.readInt();
                if (valLen <= 0 || valLen > 100000) {
                    dis.close();
                    throw new IllegalArgumentException("Strange value length");
                }
                byte[] val = new byte[valLen];
                for (int i = 0; i < valLen; i++) {
                    val[i] = dis.readByte();
                }
                hm.put(new String(key, "UTF-8"), new String(val, "UTF-8"));
            }
            dis.close();
            return hm;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while load db");
        }
    }

    public static void save(File fl, Map<String, String> hm) throws 
                                                        IllegalArgumentException {
        try {
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
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while load db");
        }
    }
}
