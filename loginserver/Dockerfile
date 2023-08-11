FROM java-app:1.8

ENV LANG="C.UTF-8"

COPY deploy/LoginServer .

RUN chmod 777 /app/*.sh
ADD docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod 777 /docker-entrypoint.sh

# 暴露开发常用的端口
EXPOSE 22 8882
CMD ["/docker-entrypoint.sh"]
