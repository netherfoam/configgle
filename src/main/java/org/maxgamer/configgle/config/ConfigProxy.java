package org.maxgamer.configgle.config;

import org.maxgamer.configgle.exception.IncompleteException;
import org.maxgamer.configgle.parser.Parser;
import org.maxgamer.configgle.parser.StandardParsers;
import org.maxgamer.configgle.util.TypeUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author netherfoam
 */
public class ConfigProxy implements InvocationHandler {
    private Map<Type, Parser<?>> parsers = new HashMap<>(StandardParsers.getParsers());
    private Scanner scanner;
    private PrintStream out;
    private Map<PreviousInvocation, PreviousResult> previous = new HashMap<>();

    public ConfigProxy(InputStream in, OutputStream out, Map<Type, Parser<?>> extraParsers) {
        this.scanner = new Scanner(in);
        this.out = new PrintStream(out);
        this.parsers.putAll(extraParsers);
    }

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            // Method invoked on Object.class, we don't proxy these methods. Otherwise, we'd
            // break a whole bunch of things like hashCode() or equals()!
            return method.invoke(o, args);
        }

        // Create our invocation object
        PreviousInvocation invocation = new PreviousInvocation(method, args);

        // Look for a previous result
        PreviousResult result = previous.get(invocation);
        if(result != null) {
            if(result.exception != null) throw result.exception;

            return result.result;
        }

        // We are asking a new question
        do {
            try {
                Object r = ask(method);
                result = new PreviousResult(r);
            } catch (Exception f) {
                result = new PreviousResult(f);

                out.println(f.getClass().getSimpleName() + ": " + f.getMessage());
            }
        } while (result.exception instanceof IllegalArgumentException);

        // Save the answer for later
        previous.put(invocation, result);

        if(result.exception != null) {
            throw result.exception;
        }

        return result.result;
    }

    private Object ask(Method method) {
        Question question = method.getAnnotation(Question.class);
        String q = question.value();

        Type type = method.getGenericReturnType();
        if (type instanceof Class && ((Class) type).isPrimitive()) {
            // int -> Integer, boolean -> Boolean etc.
            type = TypeUtil.box((Class) type);
        }

        Parser<?> parser = parsers.get(type);

        if(parser == null) {
            throw new IllegalStateException("I can't give you back a " + type + " from user input");
        }

        out.print(q + ": ");
        out.flush();

        String input = scanner.nextLine();

        while (true) {
            try {
                return parser.parse(input);
            } catch (IncompleteException e) {
                // Try append the next line for the next parse
                input = input + "\n" + scanner.nextLine();
            }
        }
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
        private final Exception exception;
        private final Object result;

        public PreviousResult(Object result) {
            this.result = result;
            this.exception = null;
        }

        public PreviousResult(Exception exception) {
            this.exception = exception;
            this.result = null;
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
