FROM openjdk:21
CMD ["gradle", "bootWar"]
ARG JAR_FILE=build/libs/*.war
COPY ${JAR_FILE} app.war
ENTRYPOINT ["java","-jar","/app.war"]