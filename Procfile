web: sh target/bin/webapp
scheduler: java $JAVA_OPTS -cp 'target/repo/org/quartz-scheduler/quartz/2.3.0/quartz-2.3.0.jar:target/repo/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar:target/classes/:dependency/*' schedulers.PoolCreationScheduler
