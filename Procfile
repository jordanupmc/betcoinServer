web: sh target/bin/webapp
scheduler: java $JAVA_OPTS -cp target/repo/org/quartz-scheduler/quartz/2.3.0/quartz-2.3.0.jar:target/repo/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar:target/classes/:dependency/*:target/repo/org/mongodb/mongodb-driver/3.8.2/mongodb-driver-3.8.2.jar:target/repo/org/postgresql/postgresql/42.2.2/postgresql-42.2.2.jar:target/repo/org/json/json/20180813/json-20180813.jar schedulers.PoolCreationScheduler
