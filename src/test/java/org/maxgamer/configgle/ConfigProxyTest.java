package org.maxgamer.configgle;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.configgle.config.Question;
import org.maxgamer.configgle.exception.IncompleteException;
import org.maxgamer.configgle.parser.AbstractParser;
import org.maxgamer.configgle.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author netherfoam
 */
public class ConfigProxyTest {
    private Parser<List<Integer>> numberParser = new AbstractParser<List<Integer>>(List.class) {
        @Override
        public List<Integer> parse(String input) throws IncompleteException {
            if(!input.endsWith("\n")) {
                throw new IncompleteException();
            }

            List<Integer> numbers = new ArrayList<>();
            for(String s : input.split("\\s")) {
                numbers.add(Integer.valueOf(s));
            }

            return numbers;
        }
    };

    @Test
    public void test() {
        String input =
                "23\n" +
                "Blue\n";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);

        TestQuestions questions = Configgle.get(in, out, TestQuestions.class, Collections.emptyMap());
        int age = questions.age();
        Assert.assertEquals("Expect age to be 23", 23, age);

        String color = questions.color();
        Assert.assertEquals("Expect color to be Blue", "Blue", color);
    }

    @Test
    public void genericTest() throws NoSuchMethodException {
        String input =
                "91\n" +
                "26\n" +
                "\n";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);

        HashMap<Type, Parser<?>> parsers = new HashMap<>();
        parsers.put(TestQuestions.class.getMethod("numbers").getGenericReturnType(), numberParser);

        TestQuestions questions = Configgle.get(in, out, TestQuestions.class, parsers);
        List<Integer> numbers = questions.numbers();
        Assert.assertEquals("Expect 91", 91, numbers.get(0).intValue());
        Assert.assertEquals("Expect 26", 26, numbers.get(1).intValue());
    }

    public interface TestQuestions {
        @Question("How old are you?")
        int age();

        @Question("Favourite colour?")
        String color();

        @Question("Type of List")
        List<Integer> numbers();
    }
}
