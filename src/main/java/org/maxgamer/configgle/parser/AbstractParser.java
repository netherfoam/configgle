package org.maxgamer.configgle.parser;

/**
 * @author netherfoam
 */
public abstract class AbstractParser<T> extends Parser<T> {
    private Class<T> type;

    public AbstractParser(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> type() {
        return type;
    }
}
