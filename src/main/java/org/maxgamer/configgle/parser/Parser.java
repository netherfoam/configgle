package org.maxgamer.configgle.parser;

import org.maxgamer.configgle.exception.IncompleteException;

/**
 * @author netherfoam
 */
public interface Parser<T> {
    T parse(String input) throws IncompleteException;
}
