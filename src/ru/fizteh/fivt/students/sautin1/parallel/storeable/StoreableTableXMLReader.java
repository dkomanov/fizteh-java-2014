package ru.fizteh.fivt.students.sautin1.parallel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Created by sautin1 on 12/10/14.
 */
public class StoreableTableXMLReader implements AutoCloseable {
    private XMLStreamReader xmlReader;

    public StoreableTableXMLReader(String serializedValue) throws XMLStreamException, ParseException {
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(serializedValue));
            if (xmlReader.hasNext()) {
                xmlReader.next();
            } else {
                throw new ParseException("Unable to read start tag", 0);
            }
            if (!xmlReader.isStartElement() || !xmlReader.getLocalName().equals(StoreableXMLUtils.ROW_START_TAG)) {
                throw new ParseException("Invalid start tag", 0);
            }
        } catch (ParseException | XMLStreamException e) {
            closeReader();
            throw e;
        }
    }

    private Object deserializeObject(Class<?> type, String serialized) throws ColumnFormatException {
        StoreableValidityChecker.checkColumnType(type);
        Object resultObject = null;
        if (type.equals(String.class)) {
            resultObject = serialized;
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            try {
                resultObject = Integer.parseInt(serialized);
            } catch (NumberFormatException e) {
                throw new ColumnFormatException("not int");
            }
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            try {
                resultObject = Long.parseLong(serialized);
            } catch (NumberFormatException e) {
                throw new ColumnFormatException("not long");
            }
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            try {
                resultObject = Byte.parseByte(serialized);
            } catch (NumberFormatException e) {
                throw new ColumnFormatException("not byte");
            }
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            try {
                resultObject = Float.parseFloat(serialized);
            } catch (NumberFormatException e) {
                throw new ColumnFormatException("not float");
            }
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            try {
                resultObject = Double.parseDouble(serialized);
            } catch (NumberFormatException e) {
                throw new ColumnFormatException("not double");
            }
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            resultObject = Boolean.parseBoolean(serialized);
        }
        return resultObject;
    }

    public Object deserializeColumn(Class<?> type) throws XMLStreamException, ParseException {
        try {
            if (xmlReader.hasNext()) {
                xmlReader.next();
            } else {
                throw new ParseException("No column start tag", 0);
            }
            if ((!xmlReader.isStartElement() || !xmlReader.getLocalName().equals(StoreableXMLUtils.COLUMN_START_TAG))
                    && (!xmlReader.getLocalName().equals(StoreableXMLUtils.EMPTY_TAG))) {
                throw new ParseException("Invalid column start tag", 0);
            }
            String tagName = xmlReader.getLocalName();
            Object newValue = null;
            if (!xmlReader.getLocalName().equals(StoreableXMLUtils.EMPTY_TAG)) {
                if (xmlReader.hasNext()) {
                    xmlReader.next();
                } else {
                    throw new ParseException("Invalid column contents", 0);
                }
                if (!xmlReader.isCharacters()) {
                    throw new ParseException("Empty column", 0);
                }
                try {
                    newValue = deserializeObject(type, xmlReader.getText());
                } catch (ColumnFormatException e) {
                    throw new ParseException("Invalid string value: " + e.getMessage(), 0);
                }
            }
            if (xmlReader.hasNext()) {
                xmlReader.next();
            } else {
                throw new ParseException("No column end tag", 0);
            }
            if (!xmlReader.getLocalName().equals(tagName) || !xmlReader.isEndElement()) {
                throw new ParseException("Invalid column end tag", 0);
            }
            return newValue;
        } catch (XMLStreamException | ParseException e) {
            closeReader();
            throw e;
        }
    }

    @Override
    public void close() throws ParseException, XMLStreamException {
        try {
            if (xmlReader.hasNext()) {
                xmlReader.next();
            } else {
                throw new ParseException("Unable to read end tag", 0);
            }
            if (!xmlReader.isEndElement() || !xmlReader.getLocalName().equals(StoreableXMLUtils.ROW_START_TAG)) {
                throw new ParseException("Invalid end tag", 0);
            }
            xmlReader.next(); // wtf, but needed
            if (xmlReader.hasNext()) {
                throw new ParseException("Serialized string is too long", 0);
            }
        } finally {
            closeReader();
        }
    }

    private void closeReader() throws XMLStreamException {
        xmlReader.close();
    }
}
