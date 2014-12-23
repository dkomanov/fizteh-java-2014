package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.ForProxy;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.JSONOperations.JSONLogCreator;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyInvocationHandler implements InvocationHandler {

    private final Object implementation;
    private final Writer writer;

    public ProxyInvocationHandler(Writer writer, Object implementation) {
        this.implementation = implementation;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        JSONLogCreator formatter = new JSONLogCreator();

        formatter.writeTimestamp();
        formatter.writeClass(implementation.getClass());
        formatter.writeMethod(method);
        formatter.writeArguments(args);

        try {
            result = method.invoke(implementation, args);
            if (!method.getReturnType().getName().equals("void")) {
                formatter.writeReturnValue(result);
            }
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            formatter.writeThrown(targetException);
            throw targetException;
        } catch (Exception e) {
            System.out.println(getClass().getSimpleName() + ": Unexpected error!");
        } finally {
            try {
                if (!method.getDeclaringClass().equals(Object.class)) {
                    writer.write(formatter.getStringRepresentation() + "\n");
                }
            } catch (IOException e) {
                System.out.println(getClass().getSimpleName() + ": Unexpected error!");
            }
        }
        return result;
    }
}
