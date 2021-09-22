package fi.luontola.clojure_native_image_agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, IOException {
        Map<String, String> args = parseArgs(agentArgs);
        String initializeClass = requiredArg(args, "initialize-class");
        Path outputDir = Paths.get(requiredArg(args, "output-dir"));

        List<String> classes = trackRecursivelyLoadedClasses(initializeClass, inst);
        classes.sort(null);

        Files.createDirectories(outputDir);
        Path file = outputDir.resolve("initialized-classes.txt");
        Files.write(file, classes);
        log("Initialized " + initializeClass + " and it loaded " + classes.size() + " classes, " +
                "which are now listed in " + file);
    }

    private static List<String> trackRecursivelyLoadedClasses(String className, Instrumentation inst) throws ClassNotFoundException {
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
        Class.forName(className);

        List<String> initializedClasses = new ArrayList<>();
        while ((className = loadedClasses.poll()) != null) {
            // The transformer detects when the JVM loads a class, but that doesn't mean
            // that the class will also be initialized. Classes which are not initialized
            // should be excluded from --initialize-at-build-time configuration, but aside
            // from injecting a tracer method call to the classes, there is no easy to way
            // to find out whether a class was initialized.
            // Instead, here we do the second workaround: Force initialize all classes that
            // were loaded.
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                // Some classes such as clojure.tools.logging$eval1 fail to load,
                // likely due to runtime code generation. It should be okay to ignore them.
                log("WARNING: Cannot initialize class: " + e);
            }
            initializedClasses.add(className);
        }

        inst.removeTransformer(transformer);
        return initializedClasses;
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

    private static void log(String message) {
        System.out.println("clojure-native-image-agent: " + message);
    }
}
