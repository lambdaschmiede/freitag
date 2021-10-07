FROM openjdk:8-alpine

COPY target/uberjar/freitag.jar /freitag/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/freitag/app.jar"]
