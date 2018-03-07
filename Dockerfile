FROM appiriodevops/ap-microservice-base

WORKDIR /data

COPY terms-microservice.jar /data/terms-microservice.jar

COPY terms-service.yaml /data/terms-service.yaml

COPY newrelic.yml /usr/local/share/newrelic/newrelic.yml

CMD java -jar /data/terms-microservice.jar server /data/terms-service.yaml

EXPOSE 8080 8081
