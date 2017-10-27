package org.maxgamer.configgle;

import org.maxgamer.configgle.config.ConfigProxy;
import org.maxgamer.configgle.config.Question;

import java.lang.reflect.Proxy;

/**
 * @author netherfoam
 */
public class Main {
    public static void main(String[] args) {
        ConfigProxy proxy = new ConfigProxy(System.in, System.out);

        TestQuestions questions = (TestQuestions) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{
                TestQuestions.class
        }, proxy);

        System.out.println("You chose: " + questions.color());
        System.out.println("You're only " + questions.age() + " years young");
        System.out.println("Hmm.. you said " + questions.color() + " right?");

    }

    public interface TestQuestions {
        @Question("How old are you?")
        public int age();

        @Question("Favourite colour?")
        public String color();
    }
}
