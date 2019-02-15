FROM postgres:latest
#RUN addgroup --gid 1000 docker && \
#    adduser --uid 1000 --ingroup docker --home /home/docker --shell /bin/sh --disabled-password --gecos "" docker
# USER=docker && GROUP=docker && \
#RUN apt-get update -y && \
#    apt-get install curl -y && \
#    apt-get clean -y && \
#    curl -SsL https://github.com/boxboat/fixuid/releases/download/v0.4/fixuid-0.4-linux-amd64.tar.gz | tar -C /usr/local/bin -xzf - && \
#    chown root:root /usr/local/bin/fixuid && \
#    chmod 4755 /usr/local/bin/fixuid && \
#    mkdir -p /etc/fixuid && \
##    printf "user: $USER\ngroup: $GROUP\n" > /etc/fixuid/config.yml
#
#
RUN localedef -i de_DE -c -f UTF-8 -A /usr/share/locale/locale.alias de_DE.UTF-8
ENV LANG de_DE.utf8
#ENTRYPOINT ["fixuid","usr/local/bin/docker-entrypoint.sh"]

#USER postgres:postgres

