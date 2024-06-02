package com.github.yoshiyoshifujii

import com.github.yoshiyoshifujii.adapter.router.Router
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object Main extends App {

  private val hostName: String = sys.env.getOrElse("HOST_NAME", "0.0.0.0")
  private val port: Int        = sys.env.getOrElse("PORT", "8080").toInt

  implicit private val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "school-api")
  implicit private val ec: ExecutionContext     = system.executionContext
  private val route: Route                      = Router().route
  private val bindingFuture: Future[Http.ServerBinding] =
    Http().newServerAt(hostName, port).bind(route).map { binding =>
      system.log.info("endpoint started at {}", binding.localAddress)
      binding
    }

  sys.addShutdownHook {
    val hardDeadline = 10.seconds
    for {
      binding <- bindingFuture
      _       <- binding.terminate(hardDeadline)
    } yield {
      system.terminate()
    }
    Await.result(system.whenTerminated, 30.seconds)
  }

}
