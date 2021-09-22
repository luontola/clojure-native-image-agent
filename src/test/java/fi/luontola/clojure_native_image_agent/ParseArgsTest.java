package fi.luontola.clojure_native_image_agent;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseArgsTest {

    @Test
    public void no_args() {
        Map<String, String> expected = new HashMap<>();
        assertEquals(expected, Agent.parseArgs(""));
    }

    @Test
    public void one_arg() {
        Map<String, String> expected = new HashMap<>();
        expected.put("foo", "1");
        assertEquals(expected, Agent.parseArgs("foo=1"));
    }

    @Test
    public void many_args() {
        Map<String, String> expected = new HashMap<>();
        expected.put("foo", "1");
        expected.put("bar", "2");
        assertEquals(expected, Agent.parseArgs("foo=1,bar=2"));
    }
}
