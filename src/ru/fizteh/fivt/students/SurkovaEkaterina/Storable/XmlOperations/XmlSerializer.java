package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.XmlOperations;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

public class XmlSerializer implements Closeable {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlWriter = null;

    public XmlSerializer() throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            xmlWriter = factory.createXMLStreamWriter(stringWriter);
            xmlWriter.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void write(Object value) throws IOException, ParseException {
        try {
            if (value == null) {
                xmlWriter.writeEmptyElement("null");
            } else {
                xmlWriter.writeStartElement("col");
                xmlWriter.writeCharacters(value.toString());
                xmlWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        try {
            xmlWriter.writeEndElement();
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public String getRepresentation() {
        return stringWriter.getBuffer().toString();
    }
}
