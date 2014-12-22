package ru.fizteh.fivt.students.SukhanovZhenya.Storable;

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
import java.util.List;
import java.util.Vector;


public class XMLCoder {
    private static Object parseType(String value, Class className) {
        switch (className.getSimpleName()) {
            case "Integer":
                return Integer.parseInt(value);
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
                throw new IllegalArgumentException("Unknown type");
        }
    }
    public static String serializeObjects(List<Object> objects) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("row");
            document.appendChild(element);
            for (Object object : objects) {
                if (object != null) {
                    Element column = document.createElement("col");
                    Text text = document.createTextNode(object.toString());
                    column.appendChild(text);
                    element.appendChild(column);
                } else {
                    Element nullElement = document.createElement("null");
                    element.appendChild(nullElement);
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (ParserConfigurationException | TransformerException e) {
            return null;
        }
    }
    public static List<Object> deserializeString(String input, List<Class<?>> types) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(input)));
            if (document.getChildNodes().getLength() != 1) {
                throw new IllegalArgumentException("More then 1 root has found");
            }
            Node row = document.getChildNodes().item(0);
            if (!row.getNodeName().equals("row")) {
                throw new IllegalArgumentException("Root must be with name row");
            }
            NodeList columns = row.getChildNodes();
            if (columns.getLength() != types.size()) {
                throw new IllegalArgumentException("Incorrect number of columns");
            }
            List<Object> result = new Vector<>();
            for (int i = 0; i < row.getChildNodes().getLength(); i++) {
                Node currentElement = columns.item(i);
                if (currentElement.getNodeName().equals("col")) {
                    if (currentElement.getChildNodes().getLength() > 1) {
                        throw new IllegalArgumentException("Only text content in col");
                    }
                    result.add(parseType(currentElement.getTextContent(), types.get(i)));
                } else {
                    if (currentElement.getNodeName().equals("null")) {
                        if (currentElement.getChildNodes().getLength() > 0) {
                            throw new IllegalArgumentException("Null is not single!");
                        }
                        result.add(null);
                    } else {
                        throw new IllegalArgumentException("Incorrect tag");
                    }
                }
            }
            return result;
        } catch (SAXException e) {
            return new Vector<>();
        } catch (IOException e) {
            return new Vector<>();
        } catch (ParserConfigurationException e) {
            return new Vector<>();
        }
    }
}
