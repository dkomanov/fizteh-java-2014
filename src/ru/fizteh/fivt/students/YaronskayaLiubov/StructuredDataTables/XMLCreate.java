package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringWriter;

/**
 * Created by luba_yaronskaya on 17.11.14.
 */
public class XMLCreate {
    private static Document document;
    public static String path;

    public static void exec(String documentPath) {
        path = documentPath;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            document = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            System.err.println(e.getMessage());
        }
        Element root = document.createElement("row");
        Element child = document.createElement("col0");
        root.appendChild(child);
        child.setTextContent("value0");
        child = document.createElement("null");
        root.appendChild(child);
        for (int i = 1; i < 10; ++i) {
            child = document.createElement("col" + i);
            root.appendChild(child);
            child.setTextContent("value" + i);
        }
        child = document.createElement("null");
        root.appendChild(child);
        document.appendChild(root);
        save();
    }

    private static void save() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            String result = writer.getBuffer().toString();
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(result.getBytes());
            fos.close();
            //System.out.println(result);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

    }
}
