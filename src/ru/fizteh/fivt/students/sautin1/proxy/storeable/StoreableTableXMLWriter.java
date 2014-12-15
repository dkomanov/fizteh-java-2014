package ru.fizteh.fivt.students.sautin1.proxy.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;


/**
 * Created by sautin1 on 12/10/14.
 */
public class StoreableTableXMLWriter {

    private void writeColumn(XMLStreamWriter xmlWriter, Object columnValue) throws XMLStreamException {
        if (columnValue == null) {
            xmlWriter.writeEmptyElement(StoreableXMLUtils.EMPTY_TAG);
        } else {
            xmlWriter.writeStartElement(StoreableXMLUtils.COLUMN_START_TAG);
            xmlWriter.writeCharacters(columnValue.toString());
            xmlWriter.writeEndElement();
        }
    }

    public String serializeStoreable(Storeable value) throws IOException, XMLStreamException {
        try (StringWriter stringWriter = new StringWriter()) {
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
            xmlWriter.writeStartElement(StoreableXMLUtils.ROW_START_TAG);
            int columnIndex = 0;
            while (true) {
                try {
                    value.getColumnAt(columnIndex);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                writeColumn(xmlWriter, value.getColumnAt(columnIndex));
                ++columnIndex;
            }
            xmlWriter.writeEndElement();
            xmlWriter.flush();
            String serializedValue = stringWriter.toString();
            xmlWriter.close();
            return serializedValue;
        }
    }
}
