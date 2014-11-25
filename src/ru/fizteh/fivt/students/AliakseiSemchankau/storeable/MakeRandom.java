package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Aliaksei Semchankau on 24.11.2014.
 */
public class MakeRandom {

    Random rand;

    MakeRandom() {
        rand = new Random();
        SerializeFunctions serializer = new SerializeFunctions();
    }

    ArrayList<Object> takeRandomValue(List<Class<?>> signature) {

        ArrayList<Object> values = new ArrayList<>();
        for (int i = 0; i < signature.size(); ++i) {
            Class<?> curClass = signature.get(i);
            if (curClass.equals(Integer.class)) {
                values.add(rand.nextInt());
            }
            if (curClass.equals(Long.class)) {
                values.add(rand.nextLong());
            }
            if (curClass.equals(Byte.class)) {
                byte[] randBytes = new byte[1];
                rand.nextBytes(randBytes);
                values.add(randBytes[0]);
            }
            if (curClass.equals(Float.class)) {
                values.add(rand.nextFloat());
            }
            if (curClass.equals(Double.class)) {
                values.add(rand.nextDouble());
            }
            if (curClass.equals(Boolean.class)) {
                int randInt = rand.nextInt();
                if (randInt % 2 == 0) {
                    values.add(true);
                }
                else {
                    values.add(false);
                }
            }
            if (curClass.equals(String.class)) {
                values.add(UUID.randomUUID().toString());
            }
        }
        return values;
    }

}
