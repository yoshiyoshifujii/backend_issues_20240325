package com.github.yoshiyoshifujii.adapter.router

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._

final case class Router() {

  def route: Route =
    path("students") {
      get {
        parameters(
          Symbol("facilitator_id").as[Int],
          Symbol("page").as[Int].?,
          Symbol("limit").as[Int].?,
          Symbol("sort").?,
          Symbol("order").?
        ) { (facilitatorId, page, limit, sort, order) =>
          complete(StatusCodes.OK, "hoge")
        }
      }
    }

}
