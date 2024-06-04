package com.github.yoshiyoshifujii

import cats.effect.IO
import com.github.yoshiyoshifujii.adapter.api.router.Router
import com.github.yoshiyoshifujii.adapter.mysql.QueriesImpl
import com.github.yoshiyoshifujii.query.Queries
import doobie.util.transactor.Transactor
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object Main extends App {

  private val hostName: String      = sys.env.getOrElse("HOST_NAME", "0.0.0.0")
  private val port: Int             = sys.env.getOrElse("PORT", "8080").toInt
  private val mysqlDriver: String   = sys.env.getOrElse("MYSQL_DRIVER", "com.mysql.cj.jdbc.Driver")
  private val mysqlUrl: String      = sys.env.getOrElse("MYSQL_URL", "jdbc:mysql://localhost:3306/school")
  private val mysqlUser: String     = sys.env.getOrElse("MYSQL_USER", "root")
  private val mysqlPassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "password")

  implicit private val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "school-api")
  implicit private val ec: ExecutionContext     = system.executionContext
  private val transactor = Transactor.fromDriverManager[IO](
    driver = mysqlDriver,
    url = mysqlUrl,
    user = mysqlUser,
    password = mysqlPassword,
    logHandler = None
  )
  private val queries: Queries = new QueriesImpl(transactor)
  private val route: Route     = Router(queries).route
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
