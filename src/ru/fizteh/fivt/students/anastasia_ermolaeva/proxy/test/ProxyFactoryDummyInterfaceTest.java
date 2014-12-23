package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.ProxyFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.TimeStampGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ProxyFactoryDummyInterfaceTest {
    private LoggingProxyFactory proxyFactory = new ProxyFactory(new DummyTimeStampGenerator());
    private InterfaceToProxy instance;
    private final Writer writer = new StringWriter();
    private static String[] logBeginnings;
    private static String logEnding = "</invoke>" + System.getProperty("line.separator");
    private StringBuilder builder = new StringBuilder();
    int counter = 0;

    class DummyTimeStampGenerator implements TimeStampGenerator {
        int counter;
        @Override
        public long getTimeStamp() {
            return (counter++);
        }
    }

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

    class InterfaceToProxyImplementation implements InterfaceToProxy {

        @Override
        public void returnVoid(int a) {
        }

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
        instance = (InterfaceToProxy) proxyFactory.wrap(writer, dummy, InterfaceToProxy.class);

        String logBeginning = "<invoke timestamp= "
                + "class=\"ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test."
                + "ProxyFactoryDummyInterfaceTest$InterfaceToProxyImplementation\"";
        logBeginnings = new String[8];
        for (int i = 0; i < 8; i++) {
            logBeginnings[i] = logBeginning.replace("timestamp=", "timestamp=\"" + i + "\"");
        }
    }

    @Test
    public void proxyReturnsVoid() throws IOException {
        instance.returnVoid(0);

        builder.append(logBeginnings[counter]);
        builder.append(" name=\"returnVoid\">");
        builder.append("<arguments><argument>0</argument></arguments>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
        builder.delete(0, builder.length());
    }

    @Test
    public void proxyReturnsString() {
        instance.getString("freak");

        builder.append(logBeginnings[counter]);

        builder.append(" name=\"getString\">");
        builder.append("<arguments><argument>freak</argument></arguments>");
        builder.append("<return>freak: done</return>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
        builder.delete(0, builder.length());
    }

    @Test
    public void proxyReturnsPrimitive() {
        instance.getPrimitive(0);

        builder.append(logBeginnings[counter]);
        builder.append(" name=\"getPrimitive\">");
        builder.append("<arguments><argument>0</argument></arguments>");
        builder.append("<return>1</return>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
        builder.delete(0, builder.length());
    }

    @Test
    public void proxyGetAndReturnsNullObject() {
        instance.getNullObject();

        builder.append(logBeginnings[counter]);
        builder.append(" name=\"getNullObject\">");
        builder.append("<arguments/>");
        builder.append("<return><null/></return>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
        builder.delete(0, builder.length());
    }

    @Test
    public void proxyGetAndReturnsListOfStrings() {
        List<String> list = Arrays.asList("freak", "geek", "others");
        instance.getListStrings(list);

        builder.append(logBeginnings[counter]);
        builder.append(" name=\"getListStrings\">");
        builder.append("<arguments><argument>"
                + "<list>"
                + "<value>freak</value>"
                + "<value>geek</value>"
                + "<value>others</value>"
                + "</list>"
                + "</argument></arguments>");
        builder.append("<return>"
                + "<list>"
                + "<value>freak</value>"
                + "<value>geek</value>"
                + "<value>others</value>"
                + "<value>done</value>"
                + "</list>"
                + "</return>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
        builder.delete(0, builder.length());
    }

    @Test
    public void proxyGetAndReturnsListOfListsOfStrings() {
        List<String> list1 = Arrays.asList("freak", "geek", "others");
        List<String> list2 = Arrays.asList("test", "bored", "got it");
        List<List<String>> lists = Arrays.asList(list1, list2);
        instance.getListWithInsertedList(lists);

        builder.append(logBeginnings[counter]);
        builder.append(" name=\"getListWithInsertedList\">");
        StringBuilder listString = new StringBuilder();
        listString.append("<list>"
                        + "<value>"
                        + "<list>"
                        + "<value>freak</value>"
                        + "<value>geek</value>"
                        + "<value>others</value>"
                        + "</list>"
                        + "</value>"
                        + "<value>"
                        + "<list>"
                        + "<value>test</value>"
                        + "<value>bored</value>"
                        + "<value>got it</value>"
                        + "</list>"
                        + "</value>"
        );
        builder.append("<arguments><argument>" + listString.toString()
                + "</list>"
                + "</argument></arguments>");
        builder.append("<return>"
                + listString.toString()
                + "<value><list/></value>"
                + "</list>"
                + "</return>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
        builder.delete(0, builder.length());
    }

    @Test
    public void proxyReturnsEmptyList() {
        instance.getEmptyListStrings();

        builder.append(logBeginnings[counter]);
        builder.append(" name=\"getEmptyListStrings\">");
        builder.append("<arguments/>");
        builder.append("<return><list/></return>");
        builder.append(logEnding);

        assertEquals(builder.toString(), writer.toString());
        counter++;
    }

    @Test(expected = IOException.class)
    public void proxyWritesInfoAboutTargetExceptionMethodThrowed() throws IOException {
        try {
            instance.throwException();
        } finally {
            builder.append(logBeginnings[counter]);
            builder.append(" name=\"throwException\">");
            builder.append("<arguments/>");
            builder.append("<thrown>java.io.IOException: : throw check</thrown>");
            builder.append(logEnding);

            assertEquals(builder.toString(), writer.toString());
            counter++;
            builder.delete(0, builder.length());
        }
    }
}
