#!/usr/bin/env bash
set -euxo pipefail

mvn release:prepare

ls -lh target/clojure-native-image-agent.jar
unzip -p target/clojure-native-image-agent.jar META-INF/maven/fi.luontola.graalvm-utils/clojure-native-image-agent/pom.properties | grep version

: Next steps:
: git push origin --tags
: git push origin main
