package org.maxgamer.configgle.config;

import org.maxgamer.configgle.parser.Parser;
import org.maxgamer.configgle.parser.StandardParsers;
import org.maxgamer.configgle.util.TypeUtil;

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
    private Map<Class<?>, Parser<?>> parsers = new HashMap<>(StandardParsers.getParsers());
    private Scanner scanner;
    private PrintStream out;
    private Map<PreviousInvocation, PreviousResult> previous = new HashMap<>();

    public ConfigProxy(InputStream in, OutputStream out, Parser... extraParsers) {
        this.scanner = new Scanner(in);
        this.out = new PrintStream(out);

        for (Parser<?> parser : extraParsers) {
            this.parsers.put(parser.type(), parser);
        }
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

        try {
            Object r = ask(method);
            result = new PreviousResult(r, null);
        } catch (Exception f) {
            result = new PreviousResult(null, f);

            out.println(f.getClass().getSimpleName() + ": " + f.getMessage());
        }

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

        Class<?> type = method.getReturnType();
        if (type.isPrimitive()) {
            // int -> Integer, boolean -> Boolean etc.
            type = TypeUtil.box(type);
        }

        Parser<?> parser = parsers.get(type);

        if(parser == null) {
            throw new IllegalStateException("I can't give you back a " + type + " from user input");
        }

        out.print(q + ": ");
        out.flush();
        String line = scanner.nextLine();

        return parser.parse(line);
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
