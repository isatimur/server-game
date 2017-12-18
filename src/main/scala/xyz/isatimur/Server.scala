package xyz.isatimur

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import xyz.isatimur.service.GameService

import scala.io.StdIn

/**
  * ${CLASS_NAME}.
  * Created at 12/14/2017 2:11 AM by
  *
  * @author Timur Isachenko
  *
  */
object Server {

  def main(args: Array[String]) {

    // needed to run the route
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future map/flatmap in the end
    implicit val executionContext = system.dispatcher

    val gameService = new GameService()
    val bindingFuture = Http().bindAndHandle(gameService.websocketRoute, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done

  }
}
