# clojure-native-image-agent for GraalVM

*Helps [GraalVM's native-image-agent](https://www.graalvm.org/reference-manual/native-image/Agent/) to be more useful
for Clojure applications.*

Because Clojure does lots of reflection and bytecode generation when a namespace is loaded, it's necessary to
use [build-time initialization](https://www.graalvm.org/reference-manual/native-image/ClassInitialization/)
for all Clojure namespaces when compiling it with GraalVM Native Image. We would like `native-image-agent` to not report
about the reflection and resource usages that happen during Clojure namespace loading. Also we would like to generate
the correct list of classes to put in `--initialize-at-build-time`, i.e. all Clojure namespaces and the Java classes
which were used during Clojure namespace loading.

That's where this Java agent comes in. You give it the main class of your Clojure application as a parameter, and it
will initialize the Clojure namespaces and track all classes that were loaded, before letting `native-image-agent` do
its thing. As a result, you get a list of classes to `--initialize-at-build-time`, and the configuration files generated
by `native-image-agent` will be shorter.

## Using

Download [clojure-native-image-agent.jar](https://github.com/luontola/clojure-native-image-agent/releases/latest/download/clojure-native-image-agent.jar)
from [releases](https://github.com/luontola/clojure-native-image-agent/releases).

Run `clojure-native-image-agent.jar` as a Java agent together with `native-image-agent`.

**The order of command line parameters matters:** `clojure-native-image-agent.jar` must be before `native-image-agent`
on the command line, so that it will run first.

    /usr/bin/java \
        -javaagent:clojure-native-image-agent.jar=initialize-class=your_clojure_app.main,output-dir=/tmp/native-image \
        -agentlib:native-image-agent=config-merge-dir=/tmp/native-image,config-write-period-secs=5 \
        -jar your-clojure-app.jar

This will generate a `native-image.properties` configuration file in the `output-dir` directory, based on the classes
that were loaded during the initialization of `initialize-class`.

The configuration is simplified for Clojure namespaces, so that only the top level package is listed. Most JDK classes
are excluded from the configuration, because GraalVM Native Image already handles those. The unredacted list of classes
is written to `initialized-classes.txt` for debugging purposes.

## Future plans

Currently, the agent initializes all classes that were loaded during initialization. But the Clojure compiler also loads
some classes (Java libraries) without initializing them. Excluding those classes from `--initialize-at-build-time` would
reduce the risk of encountering incompatible classes.

## Developing

Build `target/clojure-native-image-agent.jar `

    mvn clean package
