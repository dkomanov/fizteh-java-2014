package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

/**
 * Created by moskupols on 03.12.14.
 */
class XmlSerializer {
    String serialize(Storeable stor) throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stringWriter);

        boolean endReached = false;
        xmlWriter.writeStartElement("row");
        for (int i = 0; !endReached; ++i) {
            try {
                Object value = stor.getColumnAt(i);
                if (value == null) {
                    xmlWriter.writeEmptyElement("null");
                } else {
                    xmlWriter.writeStartElement("col");
                    xmlWriter.writeCharacters(value.toString());
                    xmlWriter.writeEndElement();
                }
            } catch (IndexOutOfBoundsException e) {
                endReached = true;
            }
        }
        xmlWriter.writeEndElement();

        xmlWriter.flush();
        return stringWriter.toString();
    }
}
