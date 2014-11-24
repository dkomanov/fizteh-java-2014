package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

public class XmlSerializer {

    public static String serializeObjectList(List<Object> objects) {
        try {
            //okey google, how to build xml java
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
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
            //remove lishniy output
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter stringWriter = new StringWriter();
            //transform DOM tree to string
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Something really bad happened in configuration files");
            return null;
        }
    }

    private static Object parseValue(String value, Class classname) throws ParseException {
        try {
            switch (classname.getSimpleName()) {
                case "Integer":
                    return Integer.parseInt(value); // Xxx.parseXxx
                case "Long":
                    return Long.parseLong(value);
                case "Byte":
                    return Byte.parseByte(value);
                case "Float":
                    return Float.parseFloat(value);
                case "Double":
                    return Double.parseDouble(value);
                case "Boolean":
                    return Boolean.parseBoolean(value);
                case "String":
                    return value;
                default:
                    throw new ParseException(classname.getSimpleName() + " is unknown type", 0);
            }
        } catch (NumberFormatException e) {
            throw new ParseException("Incorrect number format. " + e.getMessage(), 0);
        }
    }

    public static List<Object> deserializeString(String xmlInput, List<Class<?>> types)
            throws ParseException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        try {
            //okey google, how to parse xml java
            Document document = builder.parse(new InputSource(new StringReader(xmlInput)));
            //we have many rows
            if (document.getChildNodes().getLength() != 1) {
                throw new ParseException("One root in xml tree expected", 0);
            }
            //if row one
            Node row = document.getChildNodes().item(0);
            if (!row.getNodeName().equals("row")) {
                throw new ParseException("Root tag must be named row", 0);
            }
            NodeList cols = row.getChildNodes();
            //if size of cols different of columns
            if (cols.getLength() != types.size()) {
                throw new ParseException("Incorrect number of columns in string for this table", 0);
            }
            List<Object> result = new Vector<>();
            for (int i = 0; i < row.getChildNodes().getLength(); i++) {
                Node current = cols.item(i);
                if (current.getNodeName().equals("col")) {
                    //if col has tags
                    if (current.getChildNodes().getLength() > 1) {
                        throw new ParseException("<col> node must have text content only", i);
                    }
                    result.add(parseValue(current.getTextContent(), types.get(i)));
                } else if (current.getNodeName().equals("null")) {
                    //if col has tags
                    if (current.getChildNodes().getLength() > 0) {
                        throw new ParseException("<null> must be a single tag", i);
                    }
                    result.add(null);
                } else {
                    throw new ParseException("Incorrect tag inside row statement: " + current.getNodeName(), i);
                }
            }
            return result;
        } catch (SAXException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (IOException e) {
            throw new ParseException("IO error while parsing xml", 0);
        }
    }
}
