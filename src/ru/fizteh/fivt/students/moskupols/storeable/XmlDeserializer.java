package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by moskupols on 03.12.14.
 */
public class XmlDeserializer implements Deserializer {
    protected Object readCol(StoreableAtomType expectedType, XMLStreamReader xmlReader)
            throws ParseException, XMLStreamException {
        xmlReader.next();
        if (!xmlReader.isStartElement()) {
            throw new ParseException("Start element expected", -1);
        }
        switch (xmlReader.getLocalName()) {
            case "col":
                String value = "";
                if (xmlReader.next() == XMLStreamConstants.CHARACTERS) {
                    value = xmlReader.getText();
                    xmlReader.next();
                }
                if (!(xmlReader.isEndElement() && "col".equals(xmlReader.getLocalName()))) {
                    throw new ParseException("Column end expected", -1);
                }

                if (expectedType == StoreableAtomType.BOOLEAN) {
                    if ("false".equals(value) || "true".equals(value)) {
                        return Boolean.valueOf(value);
                    } else {
                        throw new ParseException("Boolean expected", -1);
                    }
                } else if (expectedType == StoreableAtomType.STRING) {
                    return value;
                } else {
                    try {
                        return expectedType.getBoxedClass().getMethod("valueOf", String.class)
                                .invoke(expectedType.getBoxedClass(), value);
                    } catch (InvocationTargetException e) {
                        throw new ParseException("Number expected: " + e.getCause().getMessage(), -1);
                    } catch (NoSuchMethodException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            case "null":
                if (xmlReader.next() != XMLStreamConstants.END_ELEMENT || !xmlReader.getLocalName().equals("null")) {
                    throw new ParseException("Empty null expected", -1);
                }
                return null;
            default:
                throw new ParseException("Either col or null expected", -1);
        }
    }

    public Storeable deserialize(List<StoreableAtomType> signature, String xml)
            throws ParseException {
        try {
            StringReader stringReader = new StringReader(xml);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(stringReader);

            Storeable ret = new StoreableImpl(signature);

            xmlReader.next();
            if (!(xmlReader.isStartElement() && "row".equals(xmlReader.getLocalName()))) {
                throw new ParseException("row expected", -1);
            }
            for (int i = 0; i < signature.size(); i++) {
                ret.setColumnAt(i, readCol(signature.get(i), xmlReader));
            }
            xmlReader.next();
            if (!(xmlReader.isEndElement() && "row".equals(xmlReader.getLocalName()))) {
                throw new ParseException("row end expected", -1);
            }

            return ret;
        } catch (XMLStreamException e) {
            throw new ParseException(e.getMessage(), -1);
        }
    }
}
