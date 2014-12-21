package ru.fizteh.fivt.students.torunova.proxy.tests;


/**
 * Created by nastya on 08.12.14.
 */
public interface TestInterface  {
    Iterable methodReturnsIterable(Iterable iterable);
    void methodReturnsVoid();
    Object methodWithException(TestException e) throws TestException;
    int methodReturnsInt(int i);
    short methodReturnsShort(short sh);
    long methodReturnsLong(long l);
    float methodReturnsFloat(float f);
    double methodReturnsDouble(double d);
    char methodReturnsChar(char ch);
    byte methodReturnsByte(byte b);
    boolean methodReturnsBool(boolean bool);
}
