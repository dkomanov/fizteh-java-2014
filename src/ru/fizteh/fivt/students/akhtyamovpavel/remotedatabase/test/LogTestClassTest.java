package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LogTestClassTest {
    static LoggerFactory factory;
    StringWriter writer;
    LogTestInterface testInterface;
    static DocumentBuilderFactory builderFactory;
    SAXParser parser;

    @BeforeClass
    public static void initFactory() {
        factory = new LoggerFactory();
        builderFactory = DocumentBuilderFactory.newInstance();
    }

    @Before
    public void initWrapper() {
        writer = new StringWriter();
        testInterface = (LogTestInterface) factory.wrap(writer, new LogTestClass(), LogTestInterface.class);

    }

    Document initDocumentParser() {
        InputSource source = new InputSource();
        source.setCharacterStream(new StringReader(writer.toString()));
        try {
            return builderFactory.newDocumentBuilder().parse(source);
        } catch (SAXException e) {
            fail();
        } catch (IOException e) {
            fail();
        } catch (ParserConfigurationException e) {
            fail();
        }
        return null;
    }

    NodeList getArgumentsList(Document document, int expectedSize) {
        NodeList argumentsList = document.getElementsByTagName("argument");
        assertEquals(expectedSize, argumentsList.getLength());
        return argumentsList;
    }

    @Test
    public void testNoArgumentsMethod() throws Exception {

        testInterface.noArgumentsMethod();
        Document document = initDocumentParser();
        NodeList arguments = document.getElementsByTagName("arguments");
        NodeList argumentList = getArgumentsList(document, 1);
        assertEquals("null", argumentList.item(0).getChildNodes().item(0).getNodeName());
        NodeList invokeList = document.getElementsByTagName("invoke");
        assertNotNull(invokeList.item(0).getAttributes().getNamedItem("timestamp"));
    }

    @Test
    public void testNumericArgumentMethod() throws Exception {
        testInterface.numericArgumentMethod(5);
        Document document = initDocumentParser();
        NodeList argumentsList = getArgumentsList(document, 1);
        assertEquals("5", argumentsList.item(0).getTextContent());
    }

    @Test
    public void testListArgumentMethod() throws Exception {
        List<Object> list = new ArrayList<>();
        list.add(5);
        list.add("OK");
        testInterface.listArgumentMethod(list);
        Document document = initDocumentParser();
        NodeList argumentsList = getArgumentsList(document, 1);
        assertEquals("list", argumentsList.item(0).getChildNodes().item(0).getNodeName());
        NodeList listElements = argumentsList.item(0).getChildNodes().item(0).getChildNodes();
        assertEquals(2, listElements.getLength());
        assertEquals("5", listElements.item(0).getChildNodes().item(0).getTextContent());
        assertEquals("OK", listElements.item(1).getChildNodes().item(0).getTextContent());
    }

    @Test
    public void testManyArgumentsMethod() throws Exception {
        testInterface.manyArgumentsMethod(3, 5);
        Document document = initDocumentParser();
        NodeList argumentsList = getArgumentsList(document, 2);
        assertEquals("3", argumentsList.item(0).getTextContent());
        assertEquals("5", argumentsList.item(1).getTextContent());
        NodeList resultList = document.getElementsByTagName("return");
        assertEquals(1, resultList.getLength());
        assertEquals("8", resultList.item(0).getTextContent());
    }

    @Test
    public void testExceptionMethod() throws Exception {
        try {
            testInterface.exceptionMethod();
            fail();
        } catch (Exception e) {
            Document document = initDocumentParser();
            NodeList thrownList = document.getElementsByTagName("thrown");
            assertEquals(1, thrownList.getLength());
            assertEquals("java.io.IOException: test exception", thrownList.item(0).getTextContent());
        }

    }

    @Test
    public void testVoidClassReturningMethod() throws Exception {
        testInterface.voidClassReturningMethod();
        Document document = initDocumentParser();
        NodeList resultsList = document.getElementsByTagName("return");
        assertEquals(0, resultsList.getLength());
    }

    @Test
    public void testNullReturningMethod() throws Exception {
        testInterface.nullReturningMethod();
        Document document = initDocumentParser();
        NodeList returnList = document.getElementsByTagName("return");
        assertEquals(1, returnList.getLength());
        assertEquals("null", returnList.item(0).getChildNodes().item(0).getNodeName());
    }

    @Test
    public void testCyclicMethod() throws Exception {
        testInterface.cyclicMethod();
        Document document = initDocumentParser();
        NodeList resultsList = document.getElementsByTagName("return");
        assertEquals(1, resultsList.getLength());
        Node returnListContainer = resultsList.item(0).getChildNodes().item(0);
        assertEquals("list", returnListContainer.getNodeName());
        assertEquals(2, returnListContainer.getChildNodes().getLength());
        NodeList returnValues = returnListContainer.getChildNodes();
        assertEquals("cyclic", returnValues.item(0).getChildNodes().item(0).getTextContent());
        assertEquals("4", returnValues.item(1).getChildNodes().item(0).getTextContent());
    }
}
