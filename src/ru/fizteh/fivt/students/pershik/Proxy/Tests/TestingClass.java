package ru.fizteh.fivt.students.pershik.Proxy.Tests;

import java.io.IOException;

/**
 * Created by pershik on 11/26/14.
 */
public class TestingClass implements TestingInterface {
    @Override
    public void firstMethod() {
        System.out.println("1");
    }

    @Override
    public int secondMethod() {
        System.out.println("2");
        return 0;
    }

    @Override
    public void exceptionMethod() throws IOException {
        throw new IOException("hi");
    }

}
