package filemap;

/**
 * Created by Maxim on 07.10.2014.
 */
public class Get extends FileMapMain {
    protected void getFunction() {
        if (cmdBuffer.length == 2) {
            String k = cmdBuffer[1];
            String v = storage.get(k);
            if (v != null) {
                System.out.println("found");
                System.out.println(v);
            } else {
                System.out.println("not found");
            }
        } else {
            System.err.println("incorrect syntax");
        }
    }
}

