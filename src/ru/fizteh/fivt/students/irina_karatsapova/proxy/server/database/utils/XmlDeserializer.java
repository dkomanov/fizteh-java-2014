package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ColumnFormatException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.text.ParseException;

public class XmlDeserializer {
    String xmlRepresentation;
    XMLStreamReader xmlReader;

    public XmlDeserializer(String xmlRepresentation) throws ParseException {
        this.xmlRepresentation = xmlRepresentation;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));
        } catch (XMLStreamException e) {
            throw new ParseException("error at creating xmlReader", 0);
        }
    }


    public void start() throws ParseException {
            try {
                if (!xmlReader.hasNext()) {
                    throw new ParseException("empty", 0);
                }
                int eventType = xmlReader.next();
                if (eventType != XMLStreamConstants.START_ELEMENT) {
                    throw new ParseException("no header tag", 0);
                }

                if (!xmlReader.getName().getLocalPart().equals("row")) {
                    throw new ParseException("wrong header tag at the start", 0);
                }
            } catch (XMLStreamException e) {
                throw new ParseException("error at reading header start tag", 0);
            }
    }

    public Object getNext(Class expectedType) throws ColumnFormatException, IndexOutOfBoundsException, ParseException {
        Object value;
        try {
            int eventType = xmlReader.next();
            if (xmlReader.getName().getLocalPart().equals("null")) {
                value = null;
                if (eventType != XMLStreamConstants.START_ELEMENT
                        || xmlReader.next() != XMLStreamConstants.END_ELEMENT) {
                    throw new ParseException("wrong tags around null", 0);
                }
            } else {
                if (eventType != XMLStreamConstants.START_ELEMENT
                        || !xmlReader.getName().getLocalPart().equals("col")) {
                    if (eventType == XMLStreamConstants.END_ELEMENT || eventType == XMLStreamConstants.END_DOCUMENT) {
                        throw new IndexOutOfBoundsException("too few columns");
                    } else {
                        throw new ParseException("wrong column tag at the start", 0);
                    }
                }
                eventType = xmlReader.next();
                if (eventType != XMLStreamConstants.CHARACTERS) {
                    throw new ParseException("column is empty", 0);
                }
                value = parseValue(xmlReader.getText(), expectedType);
                eventType = xmlReader.next();
                if (eventType != XMLStreamConstants.END_ELEMENT) {
                    throw new ParseException("wrong column tag at the end", 0);
                }
            }
        } catch (XMLStreamException e) {
            throw new ParseException("error at reading column", 0);
        }
        return value;
    }

    public void finish() throws ParseException {
        try {
            int eventType = xmlReader.next();
            if (eventType != XMLStreamConstants.END_ELEMENT && eventType != XMLStreamConstants.END_DOCUMENT) {
                throw new IndexOutOfBoundsException("too many columns");
            }
        } catch (XMLStreamException e) {
            throw new ParseException("Error at reading header end tag", 0);
        }
    }

    private static Object parseValue(String value, Class type) throws ParseException {
        try {
            switch (type.getSimpleName()) {
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
                    throw new ColumnFormatException("unknown type: " + type.getSimpleName());
            }
        } catch (NumberFormatException e) {
            throw new ColumnFormatException("incorrect number format");
        }
    }
}
