# akka streams

Akka streams is a library for efficient stream data processing where the data maybe too big to fit in memory. This talk will be a intro to akka streams and stream processing architectures, as well as where it fits in the software ecosystem.

This talk will be a intro to stream processing architectures and where they fit the the ecosystem as well as a intro to the akka streams library.


# ammonite
import $ivy.`com.typesafe.akka::akka-stream:2.5.2`

val x = Source(1 to 100).zipWith(Source(1 to 100))((a,b) => a+b)
x.runForeach(println) 