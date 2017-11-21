FROM node:carbon
MAINTAINER Rémi Alvergnat <remi.alvergnat@gfi.fr>

ARG GROUP_ID=1000
ARG USER_ID=1000

# Fix permissions to match host user
RUN usermod -u ${USER_ID:-1000} node && groupmod -g ${GROUP_ID:-1000} node

RUN mkdir -p /home/node/.cache/yarn && chown -R $USER_ID:$GROUP_ID /home/node/.cache
VOLUME /home/node/.cache

RUN mkdir /app && chown -R $USER_ID:$GROUP_ID /app
VOLUME /app

WORKDIR /app

USER node
