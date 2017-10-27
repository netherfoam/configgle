package org.maxgamer.configgle;

import org.junit.Test;
import org.maxgamer.configgle.config.Question;

/**
 * @author netherfoam
 */
public class ConfigProxyTest {
    @Test
    public void test() {

    }

    public interface TestQuestions {
        @Question("How old are you?")
        public int age();

        @Question("Favourite colour?")
        public String color();
    }
}
