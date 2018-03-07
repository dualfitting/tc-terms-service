FROM appiriodevops/ap-microservice-base:0.0.1

WORKDIR /data

COPY terms-microservice.jar /data/terms-microservice.jar

COPY terms-service.yaml /data/terms-service.yaml

COPY newrelic.yml /usr/local/share/newrelic/newrelic.yml

CMD java -jar /data/terms-microservice.jar server /data/terms-service.yaml

EXPOSE 8080 8081
