import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.collection.immutable
import scala.concurrent._
import scala.concurrent.duration._

object Main extends scala.App {
  HelloWorld1.out
}

object HelloWorld {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  def transform(data: String) = data.reverse

  val x = Flow[String]

  val y = Flow[String]

  val z = x.join(y)

  z

  val flow: Flow[String, ByteString, NotUsed] =
    Flow[String]
      .map(data => transform(data))
      .map(s => ByteString(s + "\n"))
      .via(Compression.gzip)

  flow.batch(10, bs => Seq(bs))((xs, bs) => xs :+ bs).take(2)

  val runnable: Future[IOResult] =
    Source(1 to 10)
      .mapAsync(1)(i => Future.successful(i))
      .map(s => ByteString(s + "\n"))
      .via(Compression.gzip)
      .runWith(FileIO.toPath(Paths.get("out.gz")))

  val result = Await.result(runnable, 1.seconds)

  result

}

object HelloWorld1 {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer(
    ActorMaterializerSettings(system).withDebugLogging(true))

  val out: Future[Done] =
    Source(0 to 4)
      .map(x => x + 1)
      .log("x")
      .map(x => x + 1)
      .log("y")
      .mapAsync(2)(Future.successful)
      .runWith(Sink.foreach(s => println(s)))

  out.onComplete(_ => system.terminate())(system.dispatcher)

  import akka.pattern.ask

  val ref: ActorRef = ???

  Source.queue()

  Flow[String]
    .mapAsync(2)(ref ? _)
}

object GraphDSLExample {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val g = RunnableGraph.fromGraph(GraphDSL.create() {
    implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val in = Source(1 to 3)
      val out = Sink.foreach[Int](x => println("x " + x))

      val bcast = builder.add(Broadcast[Int](2))
      val merge = builder.add(Merge[Int](2))

      val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

      in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
      bcast ~> f4 ~> merge

      ClosedShape
  })

  val out = g.run()

}
