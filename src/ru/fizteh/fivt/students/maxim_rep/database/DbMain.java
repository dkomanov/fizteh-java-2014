package ru.fizteh.fivt.students.maxim_rep.database;

import ru.fizteh.fivt.students.maxim_rep.database.commands.*;
import ru.fizteh.fivt.students.maxim_rep.database.commands.DBCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DbMain {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = in.readLine();
        line = StringConverter.convertToUTF8(line);
        // System.out.println(HexConverter.stringToHex(line));
        // System.out.println(HexConverter.hexToString(HexConverter.stringToHex(line)));

        String filePath = "/home/s32609/user.txt";
        // DBCommand test = new
        // Put(HexConverter.stringToHex(StringConverter.convertToUTF8("ключ")),HexConverter.stringToHex(line),
        // filePath);
        // test.execute();
        // System.err.println(HexConverter.hexToString("D0BAD0BBD18ED187D0B7D0BDD0B0D187D0B5D0BDD0B8D0B5000016"));
        DBCommand test2 = new List(filePath);
        test2.execute();
    }
}
