#!/usr/bin/env bash

./gradlew :backend-zuul-service:clean :backend-zuul-service:build :backend-zuul-service:buildDockerImage && \
    docker run -ti --rm -p 5555:5555 qu4rk/thesis-zuulservice:snapshot

# -ti - interactive mode (ctrl-c) to stop container
# --rm - container will be removed after stop
