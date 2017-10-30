# Configgle
Small Java too to make it easier to read user input via a proxy mechanism.

## Examples
```java
/* TestQuestions.java */
public interface TestQuestions {
    @Question("How old are you?")
    int age();

    @Question("Favourite colour?")
    String color();
}
```

```java
TestQuestions questions = Configgle.get(TestQuestions.class);

int age = questions.age();

System.out.println("Okay great, now...");
String color = questions.color();

System.out.println("Lots of " + age + " year-olds like the color " + color + "!");
```

Yields this standard output and input:
```
How old are you?: 23
Okay great, now...
Favourite colour?: Blue
Lots of 23 year-olds like the color Blue!
```

## Guide
First, create your interface class:
```java
interface MyQuestions {
}
```

Define a method, annotated with `@Question` and a human readable message as the value:
```java
@Question("Favourite color")
String color();
}
```

Create an instance through the utility method:
```java
MyQuestions questioner = Configgle.get(MyQuestions.class);
```

You can now interrogate the resulting object using:
```java
String color = questioner.color();
```

The user will be prompted for each new question.

Asking the same question twice will only prompt the user once, this is done intentionally through a caching mechanism.