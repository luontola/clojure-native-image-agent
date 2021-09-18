#!/usr/bin/env bash
set -euxo pipefail

mvn clean verify
ls -lh target/clojure-native-image-agent.jar
