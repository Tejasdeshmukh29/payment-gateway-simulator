FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/PaymentGateway-0.0.1-SNAPSHOT.jar app.jar

RUN apt-get update && apt-get install -y mysql-client

EXPOSE 8080

CMD ["sh", "-c", "ls -l && echo 'Waiting for MySQL...'; until mysql -h mysql -uroot -pdatabreach -e 'SELECT 1'; do sleep 3; done; echo 'MySQL is ready'; java -jar app.jar"]