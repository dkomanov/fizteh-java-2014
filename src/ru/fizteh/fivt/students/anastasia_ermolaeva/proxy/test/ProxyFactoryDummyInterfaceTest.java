package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.ProxyFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProxyFactoryDummyInterfaceTest {
    ProxyFactory proxyFactory = new ProxyFactory();
    InterfaceToProxy instance;
    Writer writer = new StringWriter();

    interface InterfaceToProxy {
        void returnVoid(int a);
        String getString(String a);
        int getPrimitive(int a);
        List<String> getListStrings(List<String> list);
        List<List<String>> getListWithInsertedList(List<List<String>> list);
        List<String> getEmptyListStrings();
        Object getNullObject();
        void throwException() throws IOException;
    }
    class InterfaceToProxyImplementation implements InterfaceToProxy{

        @Override
        public void returnVoid (int a) {}

        @Override
        public String getString(String a) {
            return a + ": done";
        }

        @Override
        public int getPrimitive(int a) {
            return a + 1;
        }

        @Override
        public List<String> getListStrings(List<String> list) {
            List<String> result = new ArrayList<>();
            result.addAll(list);
            result.add("done");
            return result;
        }

        @Override
        public List<List<String>> getListWithInsertedList(List<List<String>> list) {
            List<String> emptyList = new ArrayList<>();
            List<List<String>> result = new ArrayList<>();
            result.addAll(list);
            result.add(emptyList);
            return result;
        }

        @Override
        public List<String> getEmptyListStrings() {
            return new ArrayList<>();
        }

        @Override
        public Object getNullObject() {
            return null;
        }

        @Override
        public void throwException() throws IOException {
            throw new IOException(": throw check");
        }
    }

    @Before
    public void setUp() throws Exception {
        InterfaceToProxyImplementation dummy = new InterfaceToProxyImplementation();
        instance = (InterfaceToProxy)proxyFactory.wrap(writer, dummy, InterfaceToProxy.class);
    }

    @Test
    public void proxyReturnsVoid() throws IOException {
        instance.returnVoid(0);
        System.out.println(writer.toString());
    }

    @Test
    public void proxyReturnsString() {
        instance.getString("freak");
        System.out.println(writer.toString());
    }

    @Test
    public void proxyReturnsPrimitive() {
        instance.getPrimitive(0);
        System.out.println(writer.toString());
    }

    @Test
    public void proxyGetAndReturnsNullObject() {
        instance.getNullObject();
        System.out.println(writer.toString());
    }

    @Test
    public void proxyGetAndReturnsListOfStrings() {
        List<String> list = Arrays.asList("freak", "geek", "others");
        instance.getListStrings(list);
        System.out.println(writer.toString());
    }
    @Test
    public void proxyGetAndReturnsListOfListsOfStrings() {
        List<String> list1 = Arrays.asList("freak", "geek", "others");
        List<String> list2 = Arrays.asList("test", "bored", "got it");
        List<List<String>> lists = Arrays.asList(list1, list2);
        instance.getListWithInsertedList(lists);
        System.out.println(writer.toString());
    }

    @Test
    public void proxyReturnsEmptyList() {
        instance.getEmptyListStrings();
        System.out.println(writer.toString());
    }

    @Test(expected = IOException.class)
    public void proxyWritesInfoAboutTargetExceptionMethodThrowed() throws IOException{
        try {
            instance.throwException();
        } finally {
            System.out.println(writer.toString());
        }
    }
}