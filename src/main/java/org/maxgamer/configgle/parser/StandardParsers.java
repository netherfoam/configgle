package org.maxgamer.configgle.parser;

/**
 * @author netherfoam
 */
public final class StandardParsers {
    public static final Parser<String> STRING = (s) -> s;
    public static final Parser<Long> LONG = (s) -> Long.valueOf(s.trim());
    public static final Parser<Integer> INTEGER = (s) -> Integer.valueOf(s.trim());
    public static final Parser<Short> SHORT = (s) -> Short.valueOf(s.trim());
    public static final Parser<Byte> BYTE = (s) -> Byte.valueOf(s.trim());
    public static final Parser<Double> DOUBLE = (s) -> Double.valueOf(s.trim());
    public static final Parser<Float> FLOAT = (s) -> Float.valueOf(s.trim());
    public static final Parser<Character> CHARACTER = (s) -> s.charAt(0);

    public static final Parser<Boolean> BOOLEAN = (s) -> {
        String t = s.toLowerCase();

        if ("yes".startsWith(t)) return true;
        if ("no".startsWith(t)) return false;

        if("true".startsWith(t)) return true;
        if("false".startsWith(t)) return false;

        throw new IllegalArgumentException(s + " is not yes/no/true/false");
    };

    private StandardParsers() {
        // Static class, no constructors
    }
}
