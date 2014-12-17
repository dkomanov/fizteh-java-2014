package ru.fizteh.fivt.students.torunova.proxy.tests;

import java.util.Iterator;

/**
 * Created by nastya on 08.12.14.
 */
public interface TestInterface  {
    Iterable methodReturnsIterable(Iterable iterable);
    void methodReturnsVoid();
    Object methodWithException(Exception e) throws Exception;
    int methodReturnsInt(int i);
    short methodReturnsShort(short sh);
    long methodReturnsLong(long l);
    float methodReturnsFloat(float f);
    double methodReturnsDouble(double d);
    char methodRetunsChar(char ch);
    byte methodReturnsByte(byte b);
    boolean methodReturnsBool(boolean bool);
}
