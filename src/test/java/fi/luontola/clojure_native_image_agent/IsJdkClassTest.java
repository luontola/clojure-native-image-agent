package fi.luontola.clojure_native_image_agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IsJdkClassTest {

    @Test
    public void JDK_class_in_excluded_package() {
        assertTrue(Agent.EXCLUDED_PACKAGES.contains("java.lang"));
        assertTrue(Agent.isJdkClass("java.lang.String"));
    }

    @Test
    public void JDK_class_in_subpackage_of_excluded_package() {
        assertTrue(Agent.EXCLUDED_PACKAGES.contains("java.lang"));
        assertFalse(Agent.EXCLUDED_PACKAGES.contains("java.lang.foo"));
        assertTrue(Agent.isJdkClass("java.lang.foo.Bar"));
    }

    @Test
    public void third_party_class_in_sibling_package_of_excluded_package() {
        assertTrue(Agent.EXCLUDED_PACKAGES.contains("java.lang"));
        assertFalse(Agent.EXCLUDED_PACKAGES.contains("java.foo"));
        assertFalse(Agent.isJdkClass("java.foo.Bar"));
    }
}
