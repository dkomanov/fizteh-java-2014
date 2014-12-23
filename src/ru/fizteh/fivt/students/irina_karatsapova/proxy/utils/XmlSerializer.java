package ru.fizteh.fivt.students.irina_karatsapova.proxy.utils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;

public class XmlSerializer {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlWriter;

    public XmlSerializer() throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            xmlWriter = factory.createXMLStreamWriter(stringWriter);
        } catch (XMLStreamException e) {
            throw new IOException("Error at creating xmlWriter");
        }
    }

    public  void start() throws IOException {
        try {
            xmlWriter.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("Error at starting with header tag");
        }
    }

    public void write(Object value) throws IOException {
        try {
            if (value == null) {
                xmlWriter.writeEmptyElement("null");
            } else {
                xmlWriter.writeStartElement("col");
                xmlWriter.writeCharacters(value.toString());
                xmlWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new IOException("Error at " + value.toString());
        }
    }

    public void finish() throws IOException {
        try {
            xmlWriter.writeEndElement();
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("Error at finishing with header tag");
        }
    }

    public String getResult() {
        return stringWriter.getBuffer().toString();
    }
}
