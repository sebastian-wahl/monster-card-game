FROM postgres
ENV POSTGRES_DB postgres
ENV POSTGRES_USER admin
ENV POSTGRES_PASSWORD password

COPY src/main/resources/schema.sql /docker-entrypoint-initdb.d/
# build image (same folder as image): docker docker build -t mcg_database_image ./
# run image: docker run -d --name mcg_database -p 5432:5432 mcg_database_image
# to enter docker postgres: psql postgresql://admin:1234@localhost:5432/postgres