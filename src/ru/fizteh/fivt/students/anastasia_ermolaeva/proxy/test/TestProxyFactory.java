package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.ProxyFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

public class TestProxyFactory implements LoggingProxyFactory {
    public static final String TEST_TIMESTAMP = "1";
    public TestProxyFactory() {
    }

    static class LoggerInvocationHandler implements InvocationHandler {
        private final Object target;
        private final Writer loggerWriter;
        private static final String INVOKE = "invoke";
        private static final String TIMESTAMP = "timestamp";
        private static final String CLASS = "class";
        private static final String NAME = "name";
        private static final String ARGUMENTS = "arguments";
        private static final String ARGUMENT = "argument";
        private static final String THROWN = "thrown";
        private static final String RETURN = "return";
        private static final String LIST = "list";
        private static final String VALUE = "value";
        private static final String NULL = "null";

        LoggerInvocationHandler(Object target, Writer loggerWriter) {
            this.target = target;
            this.loggerWriter = loggerWriter;
        }

        private boolean isObjectMethod(Method method) {
            try {
                Object.class.getMethod(method.getName());
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }

        private void writeIterable(Iterable iterable, XMLStreamWriter writer) throws XMLStreamException {
            Iterator iterator = iterable.iterator();
            if (!iterator.hasNext()) {
                writer.writeEmptyElement(LIST);
            } else {
                writer.writeStartElement(LIST);
                for (Object value : iterable) {
                    writer.writeStartElement(VALUE);
                    if (value == null) {
                        writer.writeEmptyElement(NULL);
                    } else {
                        if (value instanceof Iterable) {
                            writeIterable((Iterable) value, writer);
                        } else {
                            writer.writeCharacters(value.toString());
                        }
                    }
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
        }

        @Override
        public String toString() {
            return target.toString();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isObjectMethod = isObjectMethod(method);
            if (isObjectMethod) {
                try {
                    method.invoke(this, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(loggerWriter);
            try {
                writer.writeStartElement(INVOKE);
                writer.writeAttribute(TIMESTAMP, TEST_TIMESTAMP);
                writer.writeAttribute(CLASS, target.getClass().getName());
                writer.writeAttribute(NAME, method.getName());
                if (args == null || args.length == 0) {
                    writer.writeEmptyElement(ARGUMENTS);
                } else {
                    writer.writeStartElement(ARGUMENTS);
                    for (Object argument : args) {
                        writer.writeStartElement(ARGUMENT);
                        if (argument instanceof Iterable) {
                            writeIterable((Iterable) argument, writer);
                        } else {
                            if (argument == null) {
                                writer.writeEmptyElement(NULL);
                            } else {
                                writer.writeCharacters(argument.toString());
                            }
                        }
                        writer.writeEndElement();
                    }
                    writer.writeEndElement();
                }
                try {
                    boolean isAccessible = method.isAccessible();
                    method.setAccessible(true);
                    Object returned = method.invoke(target, args);
                    method.setAccessible(isAccessible);
                    if (!method.getReturnType().equals(void.class)) {
                        writer.writeStartElement(RETURN);
                        if (returned instanceof Iterable) {
                            writeIterable((Iterable) returned, writer);
                        } else {
                            if (returned == null) {
                                writer.writeEmptyElement(NULL);
                            } else {
                                writer.writeCharacters(returned.toString());
                            }
                        }
                        writer.writeEndElement();
                    }
                    return returned;
                } catch (InvocationTargetException e) {
                    Throwable targetException = e.getTargetException();

                    writer.writeStartElement(THROWN);
                    writer.writeCharacters(targetException.getClass().getName());
                    writer.writeCharacters(": ");
                    writer.writeCharacters(targetException.getMessage());
                    writer.writeEndElement();
                    throw targetException;
                } finally {
                    writer.writeEndElement();
                    writer.writeCharacters(System.getProperty("line.separator"));
                    writer.flush();
                    writer.close();
                }
            } catch (XMLStreamException xml) {
                loggerWriter.write("Problems with logging the method info: " + method.getName());
                return null;
            }
        }


    }

    /**
     * Создаёт класс-обёртку вокруг объекта <code>implementation</code>, которая при вызове
     * методов интерфейса <code>interfaceClass</code> выполняет логирование аргументов и результата
     * вызова методов.
     * <p>
     * Класс-обёртка не имеет права выбрасывать свои исключения, но обязана выбрасывать те же самые
     * исключения, что выбрасывает оригинальный класс.
     * <p>
     * Класс-обёртка должен быть потокобезопасным.
     *
     * @param writer         Объект, в который ведётся запись лога.
     * @param implementation Объект, реализующий интерфейс <code>interfaceClass</code>.
     * @param interfaceClass Класс интерфейса, методы которого должны выполнять запись в лог.
     * @return Объект, реализующий интерфейс <code>interfaceClass</code>, при вызове методов которого
     * выполняется запись в лог.
     * @throws IllegalArgumentException Если любой из переданных аргументов null или имеет некорректное значение.
     */
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        Utility.checkIfObjectsNotNull(writer, implementation, interfaceClass);
        checkInterface(interfaceClass);
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass}, new LoggerInvocationHandler(implementation, writer));
    }

    private void checkInterface(Class<?> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass + " is not an interface");
        }
    }
}
