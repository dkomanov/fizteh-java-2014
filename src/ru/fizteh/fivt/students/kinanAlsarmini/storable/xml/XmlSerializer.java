package ru.fizteh.fivt.students.kinanAlsarmini.storable.xml;

import ru.fizteh.fivt.students.kinanAlsarmini.storable.StoreableUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

public class XmlSerializer implements Closeable {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter writer = null;

    public XmlSerializer() throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            writer = factory.createXMLStreamWriter(stringWriter);
            writer.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void write(Object value) throws IOException, ParseException {
        try {
            writer.writeStartElement("col");

            if (value == null) {
                writer.writeStartElement("null");
                writer.writeEndElement();
            } else {
                StoreableUtils.checkValue(value, value.getClass());
                writer.writeCharacters(value.toString());
            }

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        try {
            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public String getRepresentation() {
        return stringWriter.getBuffer().toString();
    }
}
