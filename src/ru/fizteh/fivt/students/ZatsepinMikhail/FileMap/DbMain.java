package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mikhail on 26.09.14.
 */
public class DbMain {
    public static void main(String[] args) {
        System.out.println(System.getProperty("db.file"));
        try (FileOutputStream outStream =
                     new FileOutputStream(System.getProperty("db.file"))) {
            FileMap myFileMap = new FileMap();
            myFileMap.addCommand(new Exit());
            myFileMap.addCommand(new Put());
            myFileMap.addCommand(new Get());
            myFileMap.addCommand(new List());
            myFileMap.addCommand(new Remove());
            myFileMap.init(outStream);
            if (args.length > 0) {
                //myFileMap.packetMode(outStream, args);
                System.out.println("packet mode here");
            } else {
                myFileMap.interactiveMode(outStream);
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (IOException e) {
            System.out.println("io exception");
        } catch (NullPointerException e) {
            System.out.println("file error");
        }
    }
}
