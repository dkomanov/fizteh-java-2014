package ru.fizteh.fivt.students.VasilevKirill.proxy.structures;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by Kirill on 29.11.2014.
 */
public class MyLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null || implementation == null || interfaceClass == null) {
            return null;
        }
        Class[] interfaces = new Class[1];
        interfaces[0] = interfaceClass;
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                interfaces, new MyProxyLogger(implementation, writer));
    }

    /*public void recursiveListWrap(XMLStreamWriter xmlWriter, Class arg) {
        try {
            if () {
                xmlWriter.writeStartElement("argument");
                xmlWriter.writeCharacters(arg.toString());
                xmlWriter.writeEndElement();
                return;
            }
            Method[] methods = arg.getMethods();
            Iterator it = null;
            for (int i = 0; i < methods.length; ++i) {
                if (methods[i].getName().equals("iterator")) {
                    it = (Iterator) methods[i].invoke(arg.newInstance(), null);
                }
            }
            if (it == null) return;
            while (it.hasNext()) {
                recursiveListWrap(xmlWriter, it.);
            }
        } catch (Exception e) {
            int a = 1;
        }
    }

    /*public boolean isIterable(Class arg) {
        if (arg == null) {
            return false;
        }
        Class[] interfaces = arg.getInterfaces();
        boolean flag = false;
        for (Class it: interfaces) {
            if (it.equals(Iterable.class)) {
                flag = true;
            }
        }
        return flag;
    }*/
}
