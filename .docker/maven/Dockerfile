FROM maven

ARG GROUP_ID=1000
ARG USER_ID=1000

RUN mkdir /maven-repository
COPY settings.xml /usr/share/maven/conf/settings.xml

# Fix permissions to match host user
RUN groupadd -g ${GROUP_ID:-1000} maven
RUN useradd -u ${USER_ID:-1000} -g ${GROUP_ID:-1000} maven
RUN chown -R maven:maven /maven-repository
RUN chown -R maven:maven /root

RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*
COPY curl-wait.sh /usr/local/bin/curl-wait
RUN chmod +x /usr/local/bin/curl-wait

VOLUME /maven-repository

USER maven