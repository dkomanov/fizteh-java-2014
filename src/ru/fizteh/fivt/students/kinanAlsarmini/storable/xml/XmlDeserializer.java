package ru.fizteh.fivt.students.kinanAlsarmini.storable.xml;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.TypesFormatter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class XmlDeserializer {
    String xmlRepresentation;
    XMLStreamReader xmlReader;

    public XmlDeserializer(String xmlRepresentation) throws ParseException {
        this.xmlRepresentation = xmlRepresentation;

        try {
            xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));

            if (!xmlReader.hasNext()) {
                throw new ParseException("xml presentation is empty", 0);
            }

            int nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException("incorrect xml", 0);
            }

            if (!xmlReader.getName().getLocalPart().equals("row")) {
                throw new ParseException("incorrect xml", 0);
            }

        } catch (XMLStreamException e) {
            throw new ParseException("error while deserializing: " + e.getMessage(), 0);
        }
    }

    public Object getNext(Class<?> expectedType) throws ColumnFormatException, ParseException {
        Object value = null;

        try {
            int nodeType = xmlReader.next();

            if (nodeType != XMLStreamConstants.START_ELEMENT || !xmlReader.getName().getLocalPart().equals("col")) {
                throw new ParseException("incorrect xml", 0);
            }

            nodeType = xmlReader.next();

            if (nodeType == XMLStreamConstants.CHARACTERS) {
                value = TypesFormatter.parseByClass(xmlReader.getText(), expectedType);
            } else {
                if (!xmlReader.getName().getLocalPart().equals("null")) {
                    throw new ParseException("incorrect xml", 0);
                }

                value = null;
                nodeType = xmlReader.next();

                if (nodeType != XMLStreamConstants.END_ELEMENT) {
                    throw new ParseException("incorrect xml", 0);
                }
            }

            nodeType = xmlReader.next();

            if (nodeType != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException("incorrect xml", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("incorrect xml: " + e.getMessage(), 0);
        }
        return value;
    }

    public void close() throws IOException, ParseException {
        try {
            int nodeType = xmlReader.next();

            if (nodeType != XMLStreamConstants.END_ELEMENT && nodeType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("incorrect xml", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("error while deserializing: " + e.getMessage());
        }
    }
}
