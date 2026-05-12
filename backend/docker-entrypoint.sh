#!/bin/sh
set -eu

if [ ! -f target/java.args ] || find src pom.xml -newer target/java.args -print -quit 2>/dev/null | grep -q .; then
  echo "==> Source changes detected, recompiling..."
  mvn -q -DskipTests test-compile
  mvn -q -DincludeScope=runtime dependency:build-classpath -Dmdep.outputFile=target/classpath.txt

  {
    printf '%s\n' '-cp'
    printf '%s\n' "target/classes:$(cat target/classpath.txt)"
    printf '%s\n' 'com.personalfinance.tracker.PersonalFinanceTrackerApplication'
  } > target/java.args
else
  echo "==> No source changes, skipping compile"
fi

exec java @target/java.args
