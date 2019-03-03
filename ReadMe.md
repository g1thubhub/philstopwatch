Companion project to [phil_stopwatch](https://github.com/g1thubhub/phil_stopwatch)

You can build this project via:
```terminal
git clone https://github.com/g1thubhub/philstopwatch.git
cd philstopwatch
sbt clean assembly
[info] Packaging /Users/a/IdeaProjects/philstopwatch/target/scala-2.11/philstopwatch-assembly-0.1.jar
```

In case you run out of memory during the project build, try this:
```terminal
export SBT_OPTS="-Xmx1536M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xss2M"
```

Launch commands can be found in the ReadMe of [phil_stopwatch](https://github.com/g1thubhub/phil_stopwatch)