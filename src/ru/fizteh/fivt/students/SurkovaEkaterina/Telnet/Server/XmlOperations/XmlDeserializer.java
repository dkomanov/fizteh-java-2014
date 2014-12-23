package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.XmlOperations;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TypesParser;

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
                throw new ParseException("Xml representation is empty!", 0);
            }

            int eventType = xmlReader.next();
            if (eventType != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException("Incorrect xml representation!", 0);
            }

            if (!xmlReader.getName().getLocalPart().equals("row")) {
                throw new ParseException("Incorrect xml representation!", 0);
            }

        } catch (XMLStreamException e) {
            throw new ParseException("Error while deserializing: " + e.getMessage(), 0);
        }
    }

    public Object getNext(Class<?> expectedType) throws ColumnFormatException, ParseException {
        Object value = null;
        try {
            int eventType = xmlReader.next();
            if (eventType != XMLStreamConstants.START_ELEMENT || !xmlReader.getName().getLocalPart().equals("col")) {
                throw new ParseException("Incorrect xml representation!", 0);
            }
            eventType = xmlReader.next();
            if (eventType == XMLStreamConstants.CHARACTERS) {
                value = TypesParser.parseByClass(xmlReader.getText(), expectedType);
                if (value.toString().trim().equals("")) {
                    throw new ParseException("Empty column!", 0);
                }
            } else {
                if (!xmlReader.getName().getLocalPart().equals("null")) {
                    throw new ParseException("Incorrect xml representation!", 0);
                }
                value = null;
                eventType = xmlReader.next();
                if (eventType != XMLStreamConstants.END_ELEMENT) {
                    throw new ParseException("Incorrect xml representation!", 0);
                }
            }
            eventType = xmlReader.next();
            if (eventType != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException("Incorrect xml representation!", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("Incorrect xml representation: " + e.getMessage(), 0);
        }
        return value;
    }

    public void close() throws IOException, ParseException {
        try {
            int eventType = xmlReader.next();
            if (eventType != XMLStreamConstants.END_ELEMENT && eventType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("Incorrect xml representation!", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("Error while deserializing: " + e.getMessage());
        }
    }
}
