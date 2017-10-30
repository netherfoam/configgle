package org.maxgamer.configgle.parser;

/**
 * @author netherfoam
 */
public abstract class AbstractParser<T> extends Parser<T> {
    private Class<?> type;

    public AbstractParser(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class type() {
        return type;
    }
}
