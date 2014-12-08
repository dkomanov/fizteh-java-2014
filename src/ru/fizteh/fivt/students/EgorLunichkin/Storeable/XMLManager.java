package ru.fizteh.fivt.students.EgorLunichkin.Storeable;


import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class XMLManager {
    public static String serialize(List<Object> objects) throws StoreableException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element row = document.createElement("row");
            document.appendChild(row);
            for (Object object : objects) {
                if (object == null) {
                    Element nullElement = document.createElement("null");
                    row.appendChild(nullElement);
                } else {
                    Element col = document.createElement("col");
                    Text text = document.createTextNode(object.toString());
                    col.appendChild(text);
                    row.appendChild(col);
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (ParserConfigurationException | TransformerException ex) {
            throw new StoreableException("Something bad while seriaizing");
        }
    }

    public static List<Object> deserialize(String data, List<Class<?>> types)
            throws ParseException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        try {
            Document document = documentBuilder.parse(new InputSource(new StringReader(data)));
            if (document.getChildNodes().getLength() != 1) {
                throw new ParseException("One root in XML tree expected", 0);
            }
            Node row = document.getChildNodes().item(0);
            if (!row.getNodeName().equals("row")) {
                throw new ParseException("Root must have name \"row\"", 0);
            }
            NodeList cols = row.getChildNodes();
            if (cols.getLength() != types.size()) {
                throw new ParseException("Incorrect number of columns", 0);
            }
            List<Object> objectList = new ArrayList<>();
            for (int ind = 0; ind < row.getChildNodes().getLength(); ++ind) {
                Node currentNode = cols.item(ind);
                if (currentNode.getNodeName().equals("col")) {
                    if (currentNode.getChildNodes().getLength() > 1) {
                        throw new ParseException("COLUMN must have text content only", ind);
                    }
                    objectList.add(parseValue(currentNode.getTextContent(), types.get(ind)));
                } else if (currentNode.getNodeName().equals("null")) {
                    if (currentNode.getChildNodes().getLength() > 0) {
                        throw new ParseException("NULL must be a single tag", ind);
                    }
                    objectList.add(null);
                } else {
                    throw new ParseException("Incorrect tag inside row statement: " + currentNode.getNodeName(), ind);
                }
            }
            return objectList;
        } catch (SAXException | IOException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

    private static Object parseValue(String value, Class type) {
        return null;
    }
}
