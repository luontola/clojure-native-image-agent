package fi.luontola.clojure_native_image_agent;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgentTest {

    @Test
    public void test_parseArgs_no_args() {
        Map<String, String> expected = new HashMap<>();
        assertEquals(expected, Agent.parseArgs(""));
    }

    @Test
    public void test_parseArgs_one_arg() {
        Map<String, String> expected = new HashMap<>();
        expected.put("foo", "1");
        assertEquals(expected, Agent.parseArgs("foo=1"));
    }

    @Test
    public void test_parseArgs_many_args() {
        Map<String, String> expected = new HashMap<>();
        expected.put("foo", "1");
        expected.put("bar", "2");
        assertEquals(expected, Agent.parseArgs("foo=1,bar=2"));
    }
}
