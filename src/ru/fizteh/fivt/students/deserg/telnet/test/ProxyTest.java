package ru.fizteh.fivt.students.deserg.telnet.test;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.xml.sax.InputSource;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import ru.fizteh.fivt.students.deserg.telnet.proxy.DbProxyFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.junit.Assert.*;

/**
 * Created by deserg on 10.12.14.
 */
public class ProxyTest {

    private DbProxyFactory proxyFactory = new DbProxyFactory();
    private StringWriter stringWriter = new StringWriter();
    private ProxyTestInterface proxy = (ProxyTestInterface) proxyFactory.wrap(stringWriter,
            new ProxyTestImplementation(),
            ProxyTestInterface.class);

    private DocumentBuilderFactory documentFactory = new DocumentBuilderFactoryImpl();

    private Document getDocument() {
        try {
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(stringWriter.toString()));
            return documentFactory.newDocumentBuilder().parse(inputSource);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            fail();
        }
        return null;
    }

    private void xmlPrint() {

        String xmlString = stringWriter.toString();
        System.out.printf(xmlString);

    }

    @Test
    public void testMethodWithNoArgs() {

        proxy.methodWithNoArgs();
        xmlPrint();

        NodeList nodes = getDocument().getElementsByTagName("argument");

        assertEquals(0, nodes.getLength());

    }

    @Test
    public void testMethodSingleArg() {

        proxy.methodSingleArg(10);
        xmlPrint();

        NodeList nodes = getDocument().getElementsByTagName("argument");

        assertEquals(1, nodes.getLength());
        assertEquals("10", nodes.item(0).getTextContent());

    }

    @Test
    public void testMethodMultipleArg() {

        proxy.methodMultipleArg("Str1", "Str2");
        xmlPrint();

        NodeList nodes = getDocument().getElementsByTagName("argument");

        assertEquals(2, nodes.getLength());
        assertEquals("Str1", nodes.item(0).getTextContent());
        assertEquals("Str2", nodes.item(1).getTextContent());

    }

    @Test
    public void testMethodIterableArg() {

        List<String> list = new LinkedList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");

        proxy.methodIterableArg(list);

        NodeList nodes = getDocument().getElementsByTagName("list");
        assertEquals(1, nodes.getLength());

        nodes = getDocument().getElementsByTagName("value");
        assertEquals(3, nodes.getLength());

        assertEquals("item1", nodes.item(0).getTextContent());
        assertEquals("item2", nodes.item(1).getTextContent());
        assertEquals("item3", nodes.item(2).getTextContent());

    }

    @Test
    public void testMethodReturnsValue() {

        proxy.methodReturnsValue();
        xmlPrint();

        NodeList nodes = getDocument().getElementsByTagName("result");
        assertEquals(1, nodes.getLength());
        assertEquals(String.valueOf(8.124134), nodes.item(0).getTextContent());

    }


    @Test
    public void testMethodReturnsIterable() {

        proxy.methodReturnsIterable();

        NodeList nodes = getDocument().getElementsByTagName("value");
        assertEquals(2, nodes.getLength());
        assertEquals(String.valueOf(1.5), nodes.item(0).getTextContent());
        assertEquals("123321", nodes.item(1).getTextContent());

    }

    @Test
    public void testMethodThrowsException() {

        proxy.methodThrowsException();
        xmlPrint();

        NodeList nodes = getDocument().getElementsByTagName("thrown");
        assertEquals(1, nodes.getLength());

    }

    @Test
    public void testMethodCyclicReturn() {

        proxy.methodCyclicReturn();
        xmlPrint();

        NodeList nodes = getDocument().getElementsByTagName("value");
        assertEquals(2, nodes.getLength());

        nodes = getDocument().getElementsByTagName("cyclic");
        assertEquals(1, nodes.getLength());

    }
}
