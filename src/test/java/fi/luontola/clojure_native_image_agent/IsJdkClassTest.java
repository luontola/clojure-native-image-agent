package fi.luontola.clojure_native_image_agent;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IsJdkClassTest {

    private static final Set<String> exclusions = new HashSet<>(Arrays.asList(
            "com.excluded",
            "com.foo.Excluded"));

    @Test
    public void class_in_excluded_package() {
        assertTrue(Agent.containsClass("com.excluded.Foo", exclusions));
    }

    @Test
    public void class_in_subpackage_of_excluded_package() {
        assertTrue(Agent.containsClass("com.excluded.sub.Foo", exclusions));
    }

    @Test
    public void class_in_sibling_package_of_excluded_package() {
        assertFalse(Agent.containsClass("com.sibling.Foo", exclusions));
    }

    @Test
    public void class_in_parent_package_of_excluded_package() {
        assertFalse(Agent.containsClass("com.Foo", exclusions));
    }

    @Test
    public void class_in_anonymous_package() {
        assertFalse(Agent.containsClass("Foo", exclusions));
    }

    @Test
    public void same_as_excluded_class() {
        assertTrue(Agent.containsClass("com.foo.Excluded", exclusions));
    }

    @Test
    public void sibling_of_excluded_class() {
        assertFalse(Agent.containsClass("com.foo.Bar", exclusions));
    }
}
