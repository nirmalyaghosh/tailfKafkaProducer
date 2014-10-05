##TailfKafkaProducer
Reads endless log files (taking rotations into account) and publishes into a specific Kafka topic.

### Building
``` 
$ sbt assembly
```

### Testing
Assuming you already have ZooKeeper and Kafka server running,
```
$ java -cp ./target/scala-2.10/tailfKafkaProducer-assembly-0.1.jar:. net.nirmalya.tfkp.TailfKafkaProducer src/test/resources/tkfp.properties
```
Once started, the existing lines (if any) are read and published to the configured topic (`tkfp.kafka.topic` property in `tkfp.properties`).

#### Manually appending a line of text to file being _tailed_
On another console (say _console 2_), you could append a line of text to the file being tailed
```
$ echo "Random line of text" >> /tmp/somefile.txt
```
(assuming `tkfp.path` property set to `/tmp/somefile.txt`). 

#### At the consumer(s)
You should see the string (_keyed in above_) appear at the Kafka consumer(s) subscribed   to the configured topic (`tkfp.kafka.topic` property).

#### Testing file rotations
On console 2,
```
$ mv /tmp/somefile.txt /tmp/somefile.txt.1
```
Wait a while (but les than the product of the `tkfp.maxTries` & `tkfp.waitToOpenMillis` properties)
```
$ echo "Yet another random line of text" >> /tmp/somefile.txt
```
The new line of text should appear at the Kafka consumer(s).

#### Reality
More realistically, you would probably be setting the `tkfp.path` property to `/var/log/apache2/access.log` or equivalent.
