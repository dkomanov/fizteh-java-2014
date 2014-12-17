package ru.fizteh.fivt.students.torunova.proxy.tests;

/**
 * Created by nastya on 08.12.14.
 */
public class TestInterfaceImpl implements TestInterface {
    @Override
    public Iterable methodReturnsIterable(Iterable iterable) {
        return iterable;
    }

    @Override
    public void methodReturnsVoid() {
         //doing nothing.
    }

    @Override
    public Object methodWithException(Exception e) throws Exception {
        throw e;
    }

    @Override
    public int methodReturnsInt(int i) {
        return i;
    }

    @Override
    public short methodReturnsShort(short sh) {
        return sh;
    }

    @Override
    public long methodReturnsLong(long l) {
        return l;
    }

    @Override
    public float methodReturnsFloat(float f) {
        return f;
    }

    @Override
    public double methodReturnsDouble(double d) {
        return d;
    }

    @Override
    public char methodRetunsChar(char ch) {
        return ch;
    }

    @Override
    public byte methodReturnsByte(byte b) {
        return b;
    }

    @Override
    public boolean methodReturnsBool(boolean bool) {
        return bool;
    }
}
