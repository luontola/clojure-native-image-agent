package fi.luontola.clojure_native_image_agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException {
        Map<String, String> args = parseArgs(agentArgs);
        String mainClass = requiredArg(args, "main-class");

        Queue<String> loadedClasses = new ConcurrentLinkedQueue<>();
        ClassFileTransformer transformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className != null) {
                    loadedClasses.add(className.replace('/', '.'));
                }
                return null;
            }
        };
        inst.addTransformer(transformer);
        Class.forName(mainClass);
        inst.removeTransformer(transformer);

        System.out.println("loadedClasses.size() = " + loadedClasses.size());
        System.out.println("loadedClasses = " + loadedClasses);
    }

    public static Map<String, String> parseArgs(String args) {
        Map<String, String> result = new HashMap<>();
        for (String arg : args.split(",")) {
            String[] pair = arg.split("=", 2);
            if (pair.length == 2) {
                result.put(pair[0], pair[1]);
            }
        }
        return result;
    }

    private static String requiredArg(Map<String, String> args, String key) {
        String value = args.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required argument: " + key);
        }
        return value;
    }
}
