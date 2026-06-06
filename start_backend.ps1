$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2.10-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn --version
Write-Host "---"
mvn spring-boot:run -DskipTests -f "d:\ai_basketball\basketball\backend\pom.xml"