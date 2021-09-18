# clojure-native-image-agent for GraalVM

*Helps [GraalVM's native-image-agent](https://www.graalvm.org/reference-manual/native-image/Agent/) to be more useful
for Clojure applications.*

Because Clojure does lots of reflection and bytecode generation when a namespace is loaded, it's necessary to
use [build-time initialization](https://www.graalvm.org/reference-manual/native-image/ClassInitialization/)
for all Clojure namespaces when compiling it with GraalVM Native Image. We would like `native-image-agent` to not report
about the reflection and resource usages that happen during Clojure namespace loading. Also we would like to generate
the correct list of classes to put in `--initialize-at-build-time`, i.e. all Clojure classes and the Java classes which
were used during Clojure namespace loading.

This Java agent can help with that. You can give it the main class of your Clojure application as parameter, and it will
load the Clojure namespaces and track all classes that were loaded, before letting `native-image-agent` do its work. As
a result, you get a list of classes to `--initialize-at-build-time`, and the configuration files generated
by `native-image-agent` will be shorter.

## Using

Run `clojure-native-image-agent.jar` as a Java agent together with `native-image-agent`.

**The order of command line parameters matters:** `clojure-native-image-agent.jar` must be before `native-image-agent`
on the command line, so that it will run first.

    /usr/bin/java \
        -javaagent:clojure-native-image-agent.jar=initialize-class=your_clojure_app.main,output-dir=/tmp/native-image \
        -agentlib:native-image-agent=config-merge-dir=/tmp/native-image,config-write-period-secs=5 \
        -jar your-clojure-app.jar

This write in the `output-dir` a list of classes that were loaded during the initialization of `initialize-class`.

## Developing

Build `target/clojure-native-image-agent.jar `

    mvn clean package
