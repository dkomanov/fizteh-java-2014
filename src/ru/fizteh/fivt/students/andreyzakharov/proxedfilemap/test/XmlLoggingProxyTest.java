package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.XmlLoggingProxyFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class XmlLoggingProxyTest {
    private LoggingProxyFactory loggingFactory = new XmlLoggingProxyFactory();
    private StringWriter stringWriter = new StringWriter();
    private LoggingTarget target = (LoggingTarget)
            loggingFactory.wrap(stringWriter, new LoggingTargetImpl(), LoggingTarget.class);
    private DocumentBuilderFactory builderFactory = new DocumentBuilderFactoryImpl();

    private Document getDocument() {
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(stringWriter.toString()));
        try {
            return builderFactory.newDocumentBuilder().parse(inputSource);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            fail();
            return null; // unreachable
        }
    }

    @Test
    public void testNoArgs() throws Exception {
        target.methodNoArgs();

        NodeList arguments = getDocument().getElementsByTagName("argument");

        assertEquals(0, arguments.getLength());
    }

    @Test
    public void testIntegerArg() throws Exception {
        target.methodIntegerArg(42);

        NodeList arguments = getDocument().getElementsByTagName("argument");

        assertEquals(1, arguments.getLength());
        assertEquals(String.valueOf(42), arguments.item(0).getTextContent());
    }

    @Test
    public void testStringArg() throws Exception {
        target.methodStringArg("magic");

        NodeList arguments = getDocument().getElementsByTagName("argument");

        assertEquals(1, arguments.getLength());
        assertEquals("magic", arguments.item(0).getTextContent());
    }

    @Test
    public void testListArg() throws Exception {
        List<Object> list = new ArrayList<>();
        list.add(42);
        list.add("string");
        list.add(null);
        target.methodListArg(list);

        NodeList arguments = getDocument().getElementsByTagName("argument");
        assertEquals(1, arguments.getLength());
        assertEquals("list", arguments.item(0).getChildNodes().item(0).getNodeName());

        NodeList elements = arguments.item(0).getChildNodes().item(0).getChildNodes();
        assertEquals(3, elements.getLength());

        assertEquals("42", elements.item(0).getChildNodes().item(0).getTextContent());
        assertEquals("string", elements.item(1).getChildNodes().item(0).getTextContent());
        assertEquals("null", elements.item(2).getChildNodes().item(0).getNodeName());
    }

    @Test
    public void testMultipleArgs() throws Exception {
        target.methodMultipleArgs(19, 61);

        NodeList arguments = getDocument().getElementsByTagName("argument");

        assertEquals(2, arguments.getLength());
        assertEquals(String.valueOf(19), arguments.item(0).getTextContent());
        assertEquals(String.valueOf(61), arguments.item(1).getTextContent());
    }

    @Test(expected = IllegalStateException.class)
    public void testThrows() throws Exception {
        target.methodThrows();

        NodeList exceptions = getDocument().getElementsByTagName("throws");

        assertEquals(1, exceptions.getLength());
        assertEquals("", exceptions.item(0).getTextContent());
    }

    @Test
    public void testReturns() throws Exception {
        Object result = target.methodReturns();

        NodeList results = getDocument().getElementsByTagName("return");

        assertEquals(1, results.getLength());
        assertEquals(String.valueOf(result), results.item(0).getTextContent());
    }

    @Test
    public void testCyclic() throws Exception {
        target.methodCyclic();

        NodeList results = getDocument().getElementsByTagName("return");
        assertEquals(1, results.getLength());
        assertEquals("list", results.item(0).getChildNodes().item(0).getNodeName());

        NodeList elements = results.item(0).getChildNodes().item(0).getChildNodes();
        assertEquals(1, elements.getLength());
        assertEquals("value", elements.item(0).getNodeName());
        assertEquals("list", elements.item(0).getChildNodes().item(0).getNodeName());

        NodeList subelements = elements.item(0).getChildNodes().item(0).getChildNodes();
        assertEquals(1, subelements.getLength());
        assertEquals("value", subelements.item(0).getNodeName());
        assertEquals("cyclic", subelements.item(0).getChildNodes().item(0).getNodeName());
    }
}
