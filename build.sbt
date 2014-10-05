import AssemblyKeys._

assemblySettings

name := "tailfKafkaProducer"

version := "0.1"

scalaVersion := "2.10.4"

// additional libraries
libraryDependencies ++= Seq(
"org.slf4j" % "slf4j-api" % "1.7.5",
"org.slf4j" % "slf4j-simple" % "1.7.5",
"org.clapper" %% "grizzled-slf4j" % "1.0.2",
"org.apache.kafka" % "kafka_2.10" % "0.8.0"
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
)

mainClass in (Compile, packageBin) := Some("net.nirmalya.tfkp.TailfKafkaProducer")
