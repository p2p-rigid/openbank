#!/bin/sh

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)

GRADLE_USER_HOME="${GRADLE_USER_HOME:-$APP_HOME/.gradle}"
export GRADLE_USER_HOME

exec java \
  -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
  org.gradle.wrapper.GradleWrapperMain "$@"
