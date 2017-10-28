package org.maxgamer.configgle.parser;

import org.maxgamer.configgle.exception.IncompleteException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public final class StandardParsers {
    public static final Parser<String> STRING = new AbstractParser<String>(String.class) {
        @Override
        public String parse(String input) throws IncompleteException {
            return input;
        }
    };

    public static final Parser<Long> LONG = new AbstractParser<Long>(Long.class) {
        @Override
        public Long parse(String input) throws IncompleteException {
            return Long.valueOf(input.trim());
        }
    };

    public static final Parser<Integer> INTEGER = new AbstractParser<Integer>(Integer.class) {
        @Override
        public Integer parse(String input) throws IncompleteException {
            return Integer.valueOf(input.trim());
        }
    };

    public static final Parser<Short> SHORT = new AbstractParser<Short>(Short.class) {
        @Override
        public Short parse(String input) throws IncompleteException {
            return Short.valueOf(input.trim());
        }
    };

    public static final Parser<Byte> BYTE = new AbstractParser<Byte>(Byte.class) {
        @Override
        public Byte parse(String input) throws IncompleteException {
            return Byte.valueOf(input.trim());
        }
    };

    public static final Parser<Double> DOUBLE = new AbstractParser<Double>(Double.class) {
        @Override
        public Double parse(String input) throws IncompleteException {
            return Double.valueOf(input.trim());
        }
    };

    public static final Parser<Float> FLOAT = new AbstractParser<Float>(Float.class) {
        @Override
        public Float parse(String input) throws IncompleteException {
            return Float.valueOf(input.trim());
        }
    };

    public static final Parser<Character> CHARACTER = new AbstractParser<Character>(Character.class) {
        @Override
        public Character parse(String input) throws IncompleteException {
            if (input.isEmpty()) throw new IncompleteException();

            return input.charAt(0);
        }
    };

    public static final Parser<Boolean> BOOLEAN = new AbstractParser<Boolean>(Boolean.class) {
        @Override
        public Boolean parse(String s) throws IncompleteException {
            String t = s.toLowerCase();

            if ("yes".startsWith(t)) return true;
            if ("no".startsWith(t)) return false;

            if("true".startsWith(t)) return true;
            if("false".startsWith(t)) return false;

            throw new IllegalArgumentException(s + " is not yes/no/true/false");
        }
    };

    private static final Map<Class<?>, Parser<?>> PARSERS = new HashMap<>();

    static {
        PARSERS.put(String.class, STRING);
        PARSERS.put(Long.class, LONG);
        PARSERS.put(Integer.class, INTEGER);
        PARSERS.put(Short.class, SHORT);
        PARSERS.put(Byte.class, BYTE);
        PARSERS.put(Double.class, DOUBLE);
        PARSERS.put(Float.class, FLOAT);
        PARSERS.put(Character.class, CHARACTER);
        PARSERS.put(BOOLEAN.getClass(), BOOLEAN);
    }

    public static Map<Class<?>, Parser<?>> getParsers() {
        return Collections.unmodifiableMap(PARSERS);
    }

    private StandardParsers() {
        // Static class, no constructors
    }
}
