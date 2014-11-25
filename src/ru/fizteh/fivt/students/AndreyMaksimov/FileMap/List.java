package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;

import java.util.Iterator;
import java.util.Set;

public class List extends Instruction{

   public List() {
            nameOfInstruction = "list";
        }

        @Override
        public boolean startNeedInstruction(String[] arguments, DataBase needbase) throws  Exception {
            Set<String> keysSet = needbase.needdatabase.keySet();
            Iterator<String> iteratorkeysSet = keysSet.iterator();
            int checkvalue = 0;
            while (iteratorkeysSet.hasNext()) {
                if (checkvalue > 0) {
                    System.out.print(", ");
                }
                System.out.print(iteratorkeysSet.next());
                ++checkvalue;
            }
            System.out.println();
            return true;
    }
}
