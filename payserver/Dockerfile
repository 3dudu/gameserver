FROM java-app:1.8

ENV LANG="C.UTF-8"

COPY deploy/PayServer .

RUN chmod 777 /app/*.sh
ADD docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod 777 /docker-entrypoint.sh

# 暴露开发常用的端口
EXPOSE 22 10050 10051
CMD ["/docker-entrypoint.sh"]
