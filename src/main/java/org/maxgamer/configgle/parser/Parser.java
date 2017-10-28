package org.maxgamer.configgle.parser;

import org.maxgamer.configgle.exception.IncompleteException;

/**
 * @author netherfoam
 */
public abstract class Parser<T> {
    public abstract T parse(String input) throws IncompleteException;
    public abstract Class<T> type();

    public String hint() {
        return type().getSimpleName();
    }
}
