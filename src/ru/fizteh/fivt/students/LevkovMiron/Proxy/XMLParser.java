package ru.fizteh.fivt.students.LevkovMiron.Proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.util.IdentityHashMap;

/**
 * Created by Мирон on 24.11.2014 ru.fizteh.fivt.students.LevkovMiron.Proxy.
 */
public class XMLParser {

    public String parse(Object[] objects) {
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
            System.err.println("Can't parse XML");
            return null;
        }
    }

    public Element parseObject(Object object, IdentityHashMap<Object, Integer> map, Document document)
            throws ParserConfigurationException, TransformerException {
        if (object == null) {
            Element nullElement = document.createElement("null");
            document.appendChild(nullElement);
            return nullElement;
        } else {
            Element className = document.createElement(object.getClass().getSimpleName());
            if (map.containsKey(object)) {
                className.appendChild(document.createTextNode("cyclic"));
            } else {
                map.put(object, 0);
                for (Object obj : (Iterable) object) {
                    Element value = document.createElement("value");
                    className.appendChild(value);
                    if (obj == null || obj instanceof  Iterable) {
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
        Document document = builder.parse(new InputSource(new StringReader(s)));
        Node node = document.getFirstChild();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(nodeList.item(i));
        }
        return document;
    }
}
