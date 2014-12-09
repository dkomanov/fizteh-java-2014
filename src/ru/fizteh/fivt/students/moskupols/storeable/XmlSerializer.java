package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moskupols on 03.12.14.
 */
class XmlSerializer implements Serializer {
    @Override
    public String serialize(List<StoreableAtomType> signature, Storeable stor) {
        List<Object> columns = new ArrayList<>(signature.size());
        int colCount;
        for (colCount = 0; ; ++colCount) {
            Object value;
            try {
                value = stor.getColumnAt(colCount);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            columns.add(value);
            if (!signature.get(colCount).boxedClass.isInstance(value)) {
                throw new ColumnFormatException(String.valueOf(colCount));
            }
        }
        if (colCount != signature.size()) {
            throw new IndexOutOfBoundsException();
        }

        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stringWriter);

            xmlWriter.writeStartElement("row");
            for (Object col : columns) {
                if (col == null) {
                    xmlWriter.writeEmptyElement("null");
                } else {
                    xmlWriter.writeStartElement("col");
                    xmlWriter.writeCharacters(col.toString());
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();

            xmlWriter.flush();
            return stringWriter.toString();
        } catch (XMLStreamException e) {
            throw new AssertionError();
        }
    }
}
