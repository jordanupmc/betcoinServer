web: sh target/bin/webapp
scheduler: java $JAVA_OPTS -cp target/classes:target/dependency/* schedulers.PoolCreationScheduler
worker: java $JAVA_OPTS -cp target/classes:target/dependency/* schedulers.PoolCreationWorker