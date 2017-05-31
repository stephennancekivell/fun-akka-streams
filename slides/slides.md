class: center, middle

# Akka Streams

by Stephen Nancekivell

@StephenNancekiv

---

# Agenda

* what are streams
* reactive streams
* back pressure
* meta analysis
* hello world
* my favourite combinators
* graph dsl
* alpakka
* thanks / further reading
* where is it used, good use cases...

* comparison at optimization

---

# What are streams

* scala.collection.Stream
* java 8 streams
* spark streams
* iterator

---

# Properties of streams

# All Streams are
* unbounded
* processed item by item
* can perform many collection operations
** map
** filter
* cant perform
** find
** exists

Batch processing has a lot of easy optimizations, you can use batch database operations, do in memory lookups etc.
But we need to be careful with batch sizes,, batch must fit in memory, must make sure we dont crash down stream, database api, web rest api. 

Stream processing, we can make use of micro batching, but without the right tools its going to be harder. How do you join a updating-database to stream processing.

Can we describe a case, where batch process is obvious. Streaming micro batching different steps behave at different rates.

Fast data.
https://www.lightbend.com/blog/fast-data-architectures-for-streaming-applications-free-oreilly-mini-book-by-dean-wampler

---

# Consuming streams

push vs pull

push onto a queue, which could eventually overflow. or drop messages.
pull must block the subscriber,

pull is also more chatty, if asking for every message.

back pressure

---

# reactive

* Responsive
* Message driver
* Elastic
* Resilient

--- 

# Reative streams

is a interface project. Not an implementation.

Joint effort between different people in industry.

Provide common interface between different implementations. java 9 flow will be one. rxJava, slick(Relational database), mongodb driver. dot net?



* stand alone project. joint effort from different languages
* Not just an API, includes protcol, it defines the behavours of back pressure.
* java9 Flow API
* or today with scala

---

# Does it matter

* case 1 credit karma,
** https://engineering.creditkarma.com/data/akka-actors-akka-streams-when-you-should-care/?utm_content=bufferb0c4a&utm_medium=social&utm_source=twitter.com&utm_campaign=buffer
* case 2

---

# How to make

Reusable pieces

Source ~> Flow ~> Sink

---

# How to make

Reusable pieces

You can package up a graph into a flow

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

```
Flow[A]
.map(x => y)
.grouped(2)
.groupedWithin(2, 5.seconds)
.mapConcat(batch => ???: Seq[?])
.throttle(elements = 2, per = 1.seconds, maximumBurst = 10, mode = ThrottleMode.Shaping)
.batch(2, Seq(_))(_ :+ _)
.mapAsync(2)(item => ???: Future[?])
.mapAsyncUnordered(2)(item => ???: Future[?])
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

---

# Alpkka

http://github.com/akka/alpakka

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





