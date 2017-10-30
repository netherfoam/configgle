package org.maxgamer.configgle;

import org.maxgamer.configgle.config.ConfigProxy;
import org.maxgamer.configgle.parser.Parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

/**
 * TODO: Document this
 */
public class Configgle {
    public static <T> T get(InputStream in, OutputStream out, Class<T> type, Map<Type, Parser<?>> parsers) {
        ConfigProxy proxy = new ConfigProxy(in, out, parsers);

        return  (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{type}, proxy);
    }

    public static <T> T get(Class<T> type) {
        return get(System.in, System.out, type, Collections.emptyMap());
    }

    private Configgle() {
        // Static class means no constructor
    }
}
