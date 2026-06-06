@echo off
set "JAVA_HOME=C:\PROGRA~1\Microsoft\jdk-25.0.2.10-hotspot"
set "M2_HOME=C:\Users\MRCR\.m2\wrapper\dists\apache-maven-3.9.14\ed7edd442f634ac1c1ef5ba2b61b6d690b5221091f1a8e1123f5fadcc967520d"
set "PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%"
call mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dfile.encoding=UTF-8"
