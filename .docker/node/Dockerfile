FROM node:carbon
MAINTAINER Rémi Alvergnat <remi.alvergnat@gfi.fr>

ARG GROUP_ID=1000
ARG USER_ID=1000
ARG GROUP_NAME=app
ARG USER_NAME=app

RUN addgroup --system --gid $GROUP_ID $GROUP_NAME
RUN adduser --system --gid $GROUP_ID --uid $USER_ID $USER_NAME

VOLUME /.cache
RUN chown -R $USER_NAME:$GROUP_NAME /.cache

USER $USER_NAME