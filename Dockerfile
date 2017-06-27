FROM java:8
VOLUME /tmp
ADD target/productandcategoryservice-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1090","-jar","/app.jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1090"]
EXPOSE 8090 8088