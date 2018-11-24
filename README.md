# field-filter
Sample project that demonstrates how fields can be filtered out from the response
```
./gradlew clean
./gradlew build
# export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
export LOG_HOME="$HOME"
 ./gradlew :service:tomcatRun -Dlog4j2.debug
 curl -H "Content-Type: application/json" http://localhost:8080/my/service/object
 curl -H "Content-Type: application/json" http://localhost:8080/my/service/object?fields=code,type,name
 ```
Access 
