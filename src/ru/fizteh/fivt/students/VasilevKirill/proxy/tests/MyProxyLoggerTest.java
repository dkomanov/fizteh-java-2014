package ru.fizteh.fivt.students.VasilevKirill.proxy.tests;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.*;
import ru.fizteh.fivt.students.VasilevKirill.proxy.structures.MyLoggingProxyFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MyProxyLoggerTest {
    public static MyLoggingProxyFactory factory;
    public static DocumentBuilderFactory dbf;
    public StringWriter writer;
    public TestingInterface example;

    @BeforeClass
    public static void beforeClass() {
        factory = new MyLoggingProxyFactory();
        dbf = DocumentBuilderFactory.newInstance();
    }

    @Before
    public void before() {
        writer = new StringWriter();
        example = (TestingInterface) factory.wrap(writer, new TestingClass(), TestingInterface.class);
    }

    @Test
    public void noArgumentsTest() {
        try {
            example.noArgumentsMethod();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList nodes = doc.getElementsByTagName("arguments");
            assertEquals(0, nodes.item(0).getChildNodes().getLength());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void oneArgumentTest() {
        try {
            example.oneArgumentMethod("MyString");
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList args = doc.getElementsByTagName("arguments");
            NodeList childNodes = args.item(0).getChildNodes();
            assertEquals(1, childNodes.getLength());
            assertEquals("MyString", childNodes.item(0).getTextContent());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void twoArgumentTest() {
        try {
            example.twoArgumentMethod(1, 2);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList args = doc.getElementsByTagName("arguments");
            NodeList childNodes = args.item(0).getChildNodes();
            assertEquals(2, childNodes.getLength());
            assertEquals("1", childNodes.item(0).getTextContent());
            assertEquals("2", childNodes.item(1).getTextContent());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void listArgumentsTest() {
        try {
            String[] arr = {"One", "Two", "Four"};
            List<String> list = Arrays.asList(arr);
            example.listArgumentsMethod(list);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList args = doc.getElementsByTagName("arguments");
            NodeList listValues = args.item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
            assertEquals(3, listValues.getLength());
            assertEquals("One", listValues.item(0).getTextContent());
            assertEquals("Two", listValues.item(1).getTextContent());
            assertNotEquals("Three", listValues.item(2).getTextContent());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void cyclicArgumentsTest() {
        try {
            List<Object> list = new ArrayList<>();
            list.add("One");
            list.add(list);
            example.cyclicArgumentsMethod(list);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList args = doc.getElementsByTagName("arguments");
            NodeList listValues = args.item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
            assertEquals(2, listValues.getLength());
            assertEquals("One", listValues.item(0).getTextContent());
            assertEquals("cyclic", listValues.item(1).getTextContent());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void smthReturningTest() {
        try {
            example.smthReturningMethod();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList nodes = doc.getElementsByTagName("return");
            assertEquals("0", nodes.item(0).getTextContent());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void nullReturningTest() {
        try {
            example.nullReturningMethod();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList nodes = doc.getElementsByTagName("return");
            assertEquals("null", nodes.item(0).getChildNodes().item(0).getNodeName());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }

    @Test
    public void exceptionTest() {
        try {
            example.exceptionMethod();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(writer.toString()));
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList returnNodes = doc.getElementsByTagName("return");
            assertEquals(0, returnNodes.getLength());
            NodeList thrownNodes = doc.getElementsByTagName("thrown");
            assertEquals("java.io.IOException: Example of exception", thrownNodes.item(0).getTextContent());
        } catch (Exception e) {
            System.err.println("Error in XML parsing");
        }
    }
}
