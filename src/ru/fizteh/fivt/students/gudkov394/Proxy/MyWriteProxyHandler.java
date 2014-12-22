package ru.fizteh.fivt.students.gudkov394.Proxy;


import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Created by kagudkov on 30.11.14.
 */
public class MyWriteProxyHandler implements InvocationHandler {

    private Writer writer;
    private Object object;

    MyWriteProxyHandler(Writer writerTmp, Object object) {
        writer = writerTmp;
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<invoke timestamp=\"").append(System.currentTimeMillis()).
                append("\" class=\"").append(object.getClass().getCanonicalName()).
                append("\" name=\"").append(method.getName()).append("\">");
        if (args != null && args.length > 0) {
            stringBuilder.append((new ParserXML()).parse(args));
        } else {
            stringBuilder.append("<arguments/>");
        }
        try {
            Object result;
            if (args != null) {
                result = method.invoke(object, args);
            } else {
                result = method.invoke(object);
            }
            if (method.getReturnType() != void.class) {
                String res;
                if (result == null || result instanceof Iterable) {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.newDocument();
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    StringWriter stringWriter = new StringWriter();
                    transformer.transform(new DOMSource((new ParserXML()).parseObject(result,
                                    new IdentityHashMap<>(), document)),
                            new StreamResult(stringWriter));
                    res = stringWriter.toString();
                } else {
                    res = result.toString();
                }
                stringBuilder.append("<return>").append(res).append("</return>");
            }
            return result;
        } catch (InvocationTargetException e) {
            stringBuilder.append("<thrown>").append(e.getTargetException()).append("</thrown>");
            throw e;
        } finally {
            stringBuilder.append("</invoke>");
            try {
                writer.write(stringBuilder.toString());
            } catch (IOException e) {
                System.out.println("I can't write log");
            }
            writer.flush();
        }
    }
}
