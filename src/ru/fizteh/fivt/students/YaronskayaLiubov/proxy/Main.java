package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

/**
 * Created by luba_yaronskaya on 18.11.14.
 */
public class Main {
    public static void main(String[] args) {
        boolean errorOccurred;
        errorOccurred = !new MultiFileHashMap().exec(args);
        //System.out.println("COlumn type " + MultiFileHashMap.currTable.getColumnType(0).toString());
        if (errorOccurred) {
            System.exit(1);
        } else {
            System.exit(0);
        }
        /*try {
            File signFile = new File("/Users/luba_yaronskaya/Documents/workspace/fizteh-java-2014/db.dir/tablename/signature.tsv");
            FileInputStream is = new FileInputStream("/Users/luba_yaronskaya/Documents/workspace/fizteh-java-2014/db.dir/tablename/signature.tsv");
            StringBuilder sb = new StringBuilder();
            int nLength;
            byte[] buf = new byte[8000];
            while (true) {
                nLength = is.read(buf);
                if (nLength < 0) {
                    break;
                }
                sb.append(buf, 0, nLength);
                //os.write(buf, 0, nLength);
            }
            //String stringType = new String();
            //File input = new File()
            FileOutputStream fos = new FileOutputStream(stringType);
            Shell.fileCopy(fis, fos);
            fis.close();
            fos.close();
            //System.out.println(stringType);
            List<String> columnTypesList = Arrays.asList(stringType.split(" "));
            for (int i = 0; i < columnTypesList.size(); ++i) {
                System.out.println("---" + columnTypesList.get(i) + "----");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }*/
    }
}
