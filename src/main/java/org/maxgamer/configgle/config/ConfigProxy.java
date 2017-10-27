package org.maxgamer.configgle.config;

import org.maxgamer.configgle.parser.Parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author netherfoam
 */
public class ConfigProxy implements InvocationHandler {
    private Map<Class<?>, Parser<?>> parsers = new HashMap<>();
    private Scanner scanner;
    private PrintStream out;
    private Map<PreviousInvocation, PreviousResult> previous = new HashMap<>();

    public ConfigProxy(InputStream in, OutputStream out) {
        this.scanner = new Scanner(in);
        this.out = new PrintStream(out);
    }

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(o, args);
        }

        PreviousInvocation invocation = new PreviousInvocation(method, args);

        PreviousResult result = previous.get(invocation);
        if(result != null) {
            if(result.exception != null) throw result.exception;

            return result.result;
        }

        // We are asking for a new question

        //do {
            try {
                Object r = ask(method);
                result = new PreviousResult(r, null);
            } catch (Exception f) {
                result = new PreviousResult(null, f);

                out.println(f.getClass().getSimpleName() + ": " + f.getMessage());
            }
        //} while (result.exception != null);

        // Save the answer for later
        previous.put(invocation, result);

        if(result.exception != null) {
            throw result.exception;
        }

        return result.result;
    }

    public Object ask(Method method) {
        Question question = method.getAnnotation(Question.class);
        String q = question.value();

        out.print(q + ": ");
        out.println();
        out.flush();

        Class<?> type = method.getReturnType();


        if(type.isAssignableFrom(String.class)) {
            return scanner.nextLine();
        }

        try {
            if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
                return scanner.nextLong();
            }

            if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
                return scanner.nextInt();
            }

            if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
                return scanner.nextShort();
            }

            if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
                return scanner.nextByte();
            }

            if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
                return scanner.nextFloat();
            }

            if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
                return scanner.nextFloat();
            }

            if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
                // y/n works also
                String word = scanner.next();
                if ("yes".startsWith(word.toLowerCase())) {
                    return true;
                } else if ("no".startsWith(word.toLowerCase())) {
                    return false;
                }

                return Boolean.parseBoolean(word);
            }
        } finally {
            // Clears the rest of the current line because we don't want some crap empty input
            scanner.nextLine();
        }

        throw new IllegalStateException("I can't give you back a " + type + " from user input");
    }

    public static class PreviousInvocation {
        private Method method;
        private Object[] args;

        public PreviousInvocation(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, args);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            PreviousInvocation other = (PreviousInvocation) obj;
            return Objects.equals(this.method, other.method)
                    && Objects.deepEquals(this.args, other.args);
        }
    }

    public static class PreviousResult {
        private Object result;
        private Exception exception;

        public PreviousResult(Object result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }

        @Override
        public int hashCode() {
            return Objects.hash(result, exception);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            PreviousResult other = (PreviousResult) obj;
            return Objects.equals(this.result, other.result)
                    && Objects.equals(this.exception, other.exception);
        }
    }
}
