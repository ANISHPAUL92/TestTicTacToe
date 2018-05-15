FROM openjdk:8-alpine

ADD target/tictac-0.0.1-SNAPSHOT.jar /TicTacToe.jar

EXPOSE 8080

ENTRYPOINT [ "java","-Dspring.data.mongodb.uri=mongodb://mongo/rules","-jar", "/TicTacToe.jar"]