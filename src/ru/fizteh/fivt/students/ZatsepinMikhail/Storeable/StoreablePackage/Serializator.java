package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap;

import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Serializator {
    interface GetSmthSer {
        Object get(int index);
    }

    interface GetSmthDeser {
        Object get(String s) throws IllegalArgumentException;
    }

    private static HashMap<Class<?>, GetSmthSer> toFindApproriateSer;
    private static HashMap<Class<?>, GetSmthDeser> toFindApproriateDeser;
    static {
        toFindApproriateSer = new HashMap<>();
        toFindApproriateDeser = new HashMap<>();

        toFindApproriateDeser.put(Integer.class, Integer::parseInt);
        toFindApproriateDeser.put(Long.class, Long::parseLong);
        toFindApproriateDeser.put(Byte.class, Byte::parseByte);
        toFindApproriateDeser.put(Float.class, Float::parseFloat);
        toFindApproriateDeser.put(Double.class, Double::parseDouble);
        toFindApproriateDeser.put(Boolean.class, Boolean::parseBoolean);
        toFindApproriateDeser.put(String.class, String::toString);
    }

    public static String serialize(Table table, Storeable value) {
        toFindApproriateSer.put(Integer.class, value::getIntAt);
        toFindApproriateSer.put(Long.class, value::getLongAt);
        toFindApproriateSer.put(Byte.class, value::getByteAt);
        toFindApproriateSer.put(Float.class, value::getFloatAt);
        toFindApproriateSer.put(Double.class, value::getDoubleAt);
        toFindApproriateSer.put(Boolean.class, value::getBooleanAt);
        toFindApproriateSer.put(String.class, value::getStringAt);

        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
            writer.writeStartElement("row");
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                String valueInColumn;
                if (value.getColumnAt(i) == null) {
                    writer.writeEmptyElement("null");
                } else {
                    writer.writeStartElement("col");
                    valueInColumn = toFindApproriateSer.get(table.getColumnType(i)).get(i).toString();
                    writer.writeCharacters(valueInColumn);
                    writer.writeEndElement();
                }
            }
            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            return null;
        }
        return stringWriter.toString();
    }

    public static Storeable deserialize(Table table, String valueXML) throws ParseException {
        byte[] byteArray = valueXML.getBytes();
        Storeable result = ((FileMap) table).getTableProvider().createFor(table);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = inputFactory.createXMLEventReader(inputStream);
            int count = 0;
            while (reader.hasNext()) {
                XMLEvent event = (XMLEvent) reader.next();
                if (event.isStartElement()) {
                    StartElement element = event.asStartElement();
                    if (element.getName().getLocalPart().equals("col")) {
                        event = (XMLEvent) reader.next();
                        if (event.isCharacters()) {
                            String newValue = event.asCharacters().getData();
                            try {
                                result.setColumnAt(count,
                                        toFindApproriateDeser.get(table.getColumnType(count)).get(newValue));
                            } catch (NumberFormatException e) {
                                throw new ParseException(e.getMessage(), count);
                            }
                            ++count;
                        }
                    } else if (element.getName().getLocalPart().equals("null")) {
                        result.setColumnAt(count, null);
                        ++count;
                    } else if (!element.getName().getLocalPart().equals("row")) {
                        throw new ParseException(element.toString(), count);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new ParseException(e.getMessage(), -1);
        } catch (NoSuchElementException e) {
            throw new ParseException(e.getMessage(), -1);
        }
        return result;
    }
}
