FROM mysql:5.7

ENV  MYSQL_ROOT_PASSWORD=secret \
     MYSQL_DATABASE=notebook \
     MYSQL_USER=user \
     MYSQL_PASSWORD=password

COPY ./mysql/init.sql /docker-entrypoint-initdb.d
