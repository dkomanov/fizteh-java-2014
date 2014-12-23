package ru.fizteh.fivt.students.gudkov394.Proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.IdentityHashMap;

/**
 * Created by kagudkov on 30.11.14.
 */
public class ParserXML {


    public String parse(Object[] objects) throws ParseException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element arguments = document.createElement("arguments");
            document.appendChild(arguments);
            for (Object object : objects) {
                Element argument = document.createElement("argument");
                arguments.appendChild(argument);
                if (object == null || object instanceof Iterable) {
                    argument.appendChild(parseObject(object, new IdentityHashMap<>(), document));
                } else {
                    argument.appendChild(document.createTextNode(object.toString()));
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (ParserConfigurationException | TransformerException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    public Element parseObject(Object object, IdentityHashMap<Object, Integer> map, Document document)
            throws ParserConfigurationException, TransformerException {
        if (object == null) {
            return document.createElement("null");
        } else {
            Element className = document.createElement(object.getClass().getSimpleName());
            if (map.containsKey(object)) {
                className.appendChild(document.createTextNode("cyclic"));
            } else {
                map.put(object, 0);
                for (Object obj : (Iterable) object) {
                    Element value = document.createElement("value");
                    className.appendChild(value);
                    if (obj == null || obj instanceof Iterable) {
                        value.appendChild(parseObject(obj, map, document));
                    } else {
                        value.appendChild(document.createTextNode(obj.toString()));
                    }
                }
            }
            return className;
        }
    }


    public Document parseString(String s) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(s)));
    }
}
