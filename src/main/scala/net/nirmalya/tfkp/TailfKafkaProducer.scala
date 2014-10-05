package net.nirmalya.tfkp

import java.io.{ BufferedReader, File, FileInputStream, InputStreamReader }
import java.util.Properties
import com.osinka.tailf.Tail
import grizzled.slf4j.Logger
import kafka.javaapi.producer.Producer
import kafka.producer.{ KeyedMessage, ProducerConfig }

/**
 * Reads endless log files (taking rotations into account) and publishes into a
 * configured Kafka topic.
 *
 * It makes use use of Alexander Azarov's tail -f implementation.
 *
 * @author Nirmalya Ghosh
 */
object TailfKafkaProducer {

  val logger = Logger("net.nirmalya.tfkp.TailfKafkaProducer")

  def main(args: Array[String]): Unit = {
    startTailingAndProducing(args)
  }

  def startTailingAndProducing(args: Array[String]) {
    if (args.length < 1) {
      logger.error("Usage: TailfKafkaProducer <pathToConfigFile>");
      System.exit(1);
    }

    // Read the required properties
    val p = new Properties()
    try {
      val in = new FileInputStream(args(0))
      p.load(in)
      in.close();
    } catch {
      case e: Exception => System.exit(1);
    }
    val f = new File(p.getProperty("tkfp.path"))
    val maxRetries = Integer.parseInt(p.getProperty("tkfp.maxTries", "3"))
    val waitToOpen = Integer.parseInt(p.getProperty("tkfp.waitToOpenMillis", "1000"))
    val waitBetweenReads =
      Integer.parseInt(p.getProperty("tkfp.waitBetweenReadsMillis", "50"))
    // TODO need to have a cleaner way to read the properties

    // Initialize the Kafka Producer
    logger.info("Initializing the Kafka Producer")
    val kafkaConfig = new ProducerConfig(p);
    val producer = new Producer[String, String](kafkaConfig);
    val kafkaTopic = p.getProperty("tkfp.kafka.topic", "test")
    val key = p.getProperty("tkfp.source.id", "key1")

    // Start "tailing" the file and accordingly publishing to the configured topic
    logger.info(String.format("Start 'tailing' %s", f.getPath()))

    val t = Tail.follow(f, maxRetries, waitToOpen, waitBetweenReads)
    val r = new BufferedReader(new InputStreamReader(t))
    def read: Unit = {
      val line = r.readLine;
      if (line != null) {
        val mssg = new KeyedMessage[String, String](kafkaTopic, key, line);
        val outcome = producer.send(mssg);
        logger.debug("Sent message")
        read
      }
    }

    read
  }

}