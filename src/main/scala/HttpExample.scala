

import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.collection.immutable
import scala.concurrent._
import scala.concurrent.duration
import akka.http.scaladsl._
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}

import scala.util.{Random, Try}

object HttpExample {

  case class Thing(a: Int, b: String)

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val poolClientFlow =
    Http().cachedHostConnectionPool("http://httpbin.com", 80)

  val run = Source(List(1,2,3))
    .map(x => Thing(x, x.toString))
    .map(_.toString)
    .map(ByteString(_))
    .map(body => (HttpRequest(method = HttpMethods.POST, uri = "/post", entity = body), body))
    .via(Http().cachedHostConnectionPool("httpbin.org"))
    .runForeach(println)
}
