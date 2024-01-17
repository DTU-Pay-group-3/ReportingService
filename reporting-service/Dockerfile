FROM adoptopenjdk:11-jre-hotspot
COPY target/lib /usr/src/lib
COPY target/reporting-service-1.0.0-runner.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx64m -jar reporting-service-1.0.0-runner.jar
