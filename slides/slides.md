class: center, middle

# Akka Streams

by Stephen Nancekivell

@StephenNancekiv

---

# Agenda

* what are streams
* back pressure & reactive streams
* Akka streams code
* my favourite features
* Graph dsl
* alpakka open source connectors
* thanks / further reading


* comparison at optimization

---

# What are streams

* scala.collection.Stream
* Java 8 streams
* Spark streaming
* iterator
* kafka streaming aws kinesis, google pub sub
* pub subs ActiveMQ etc, 

---

# Properties of streams

* infinite data
* cant fit in memory
* processed item by item


can perform many collection operations
* map
* filter

cant perform
* find
* exists

---

# Producers Consumers

```scala
Producer ~> B ~> C ~> Consumer
```

---

# Producers Consumers

### push or pull API

push onto a queue, which could eventually overflow. or drop messages.
pull must block the subscriber,

* drop
* overflow
* block

pull is also more chatty, if asking for every message.

---

# how to optimize push / pull api

resource allocation, balance different stages



---

# back pressure

back pressure

---

So for a push API its very important that you ou know how fast each step is.
For resource allocation, to prevent errors. Which is a maintaince burden, particulalry in a changing environment. Or where network latencies are a factor.

Back pressure is the idea of slowing down a producer when the consumer cant keep up, and it has lots of nice properties.
* You dont need to worry about buffer overflow
* you can keep buffer bloat to a minimum.
* decrease latencies

---

# akka streams is push & pull

Sources comunicate demand,

Cost of pull is very low,
Slow producer effectively becomes push. It knows it was asked for 4 and only delivered 1, so it has to push 3 more before it waits for pull.


# TODO add the picture akka streams in 1 slide

---

# When this shit gets hard with out back pressure

# Async or sync streaming

Great for non blocking api's. which lead to efficent code. So your treating all code as if it were async, like it were over a network, and its so easy.

You dont have to guess how slow each step is and allocate thread pools fear each step in the process.

---

# Reative Streams

reactive-streams.org

Is a interface project. Not an implementation.
Defines how you should use the interface, the order that you should call the methods, and the rules around that.

Joint effort between different groups in industry.

Different implementations will work together. Think java to scala to haskell. You dont want to crash your friends java server because it doesnt implement reactive streams the same way.

---

# Reative Streams

* Java 9 flow
* rxJava
* slick(Relational database)
* reactive mongodb driver
* akka streams
* fs2 (formerly scalaz-streams)
* swave

---

# Does it matter

* case 1 credit karma,
** https://engineering.creditkarma.com/data/akka-actors-akka-streams-when-you-should-care

---

# Akka Streams 

Source ~> Flow ~> Sink

Reusable pieces

---

# Hello world

```scala
	implicit val system = ActorSystem()
	implicit val mat = ActorMaterializer()

	val out: Future[Done] =
		Source(List(1, 2, 3))
			.map(x => x * x)
			.runWith(Sink.foreach(s => println(s)))
```

---

# Hello world

```scala
val source: Source[Int] = ???

val flow: Flow[Int] = ???

val sink: Sink[Int] = ???
```

---

# my favourite combinators

```scala
Flow[A]
.map(x => y)
.grouped(2)
.groupedWithin(2, 1.second)
.mapConcat(item => makeBatch(item): Seq[?])
.throttle(elements = 2, per = 1.second,
          maximumBurst = 10, mode = ThrottleMode.Shaping)
.mapAsync(2)(item => ???: Future[?])
```

---

# Graph DSL

```scala 
  in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
              bcast ~> f4 ~> merge

```

---

# Graph DSL

```scala
val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
  import GraphDSL.Implicits._
  val in = Source(1 to 10)
  val out = Sink.ignore
 
  val bcast = builder.add(Broadcast[Int](2))
  val merge = builder.add(Merge[Int](2))
 
  val f1, f2, f3, f4 = Flow[Int].map(_ + 10)
 
  in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
              bcast ~> f4 ~> merge
              
  ClosedShape
})
```

Graph can be treated as a flow.

---

# Alpkka

http://github.com/akka/alpakka

---

# Futher info



# thank you



---

# batch vs streams

Counter example mongo s3 uploader task, 

A complicated process with parallel steps is impossible to optimze. The time each step takes is outside of your control.

But where you have to say stage1 needs 4 threads, stage 2 only needs 1 thread, stage3 needs 8. These are all doing IO, there performance is based on network or disk latencies which can change outside your control.

---

# promise

I have this false promise, if you use akka streams you will never have to do that again. You just describe the steps and the back pressure will take care of optimizing for you.

Big claim.
Dont actually hold me to it, I dont believe in silver bullets. But thats the ideal for we should test against, we should challenge.


# batch rant 

Batch processing has a lot of easy optimizations, you can use batch database operations, do in memory lookups etc.
But we need to be careful with batch sizes,, batch must fit in memory, must make sure we dont crash down stream, database api, web rest api. 

Stream processing, we can make use of micro batching, but without the right tools its going to be harder. How do you join a updating-database to stream processing.

Can we describe a case, where batch process is obvious. Streaming micro batching different steps behave at different rates.

Fast data.
https://www.lightbend.com/blog/fast-data-architectures-for-streaming-applications-free-oreilly-mini-book-by-dean-wampler




