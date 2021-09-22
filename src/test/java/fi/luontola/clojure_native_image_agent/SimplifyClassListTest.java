package fi.luontola.clojure_native_image_agent;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimplifyClassListTest {

    @Test
    public void excludes_JDK_classes() {
        assertEquals(Arrays.asList(),
                Agent.simplifyClassList(Arrays.asList("java.lang.String")));
    }

    @Test
    public void includes_third_party_Java_classes() {
        assertEquals(Arrays.asList("com.example.Foo"),
                Agent.simplifyClassList(Arrays.asList("com.example.Foo")));
    }
}
