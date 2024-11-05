FROM gitpod/workspace-full:2024-11-03-15-51-54

SHELL ["/bin/bash", "-c"]
RUN source "/home/gitpod/.sdkman/bin/sdkman-init.sh"  \
    && sdk install java 21.0.5-jbr < /dev/null \
