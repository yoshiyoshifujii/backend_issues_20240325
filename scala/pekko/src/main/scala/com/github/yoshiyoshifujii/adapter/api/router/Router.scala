package com.github.yoshiyoshifujii.adapter.api.router

import com.github.yoshiyoshifujii.adapter.api.handler.SchoolApiRejectionHandler
import com.github.yoshiyoshifujii.adapter.api.validation.Validation.StudentsParameterValidation
import com.github.yoshiyoshifujii.adapter.api.validation.ValidationDirectives
import com.github.yoshiyoshifujii.query.{ Queries, StudentsResponse }
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route

import scala.util.{ Failure, Success }

final case class Router(
    queries: Queries
) {

  import com.github.yoshiyoshifujii.adapter.api.support.JsonSupport._
  import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def route: Route =
    path("students") {
      get {
        handleRejections(SchoolApiRejectionHandler.rejectionHandler) {
          parameters(
            Symbol("facilitator_id").as[Int],
            Symbol("page").as[Int].?,
            Symbol("limit").as[Int].?,
            Symbol("sort").?,
            Symbol("order").?,
            Symbol("name_like").?,
            Symbol("loginId_like").?
          ) {
            (
                facilitatorId,
                maybePage,
                maybeLimit,
                maybeSort,
                maybeOrder,
                maybeNameLike,
                maybeLoginIdLike
            ) =>
              ValidationDirectives.convertWithValidation(StudentsParameterValidation)(
                (
                  facilitatorId,
                  maybePage,
                  maybeLimit,
                  maybeSort,
                  maybeOrder,
                  maybeNameLike,
                  maybeLoginIdLike
                )
              ) { studentsRequest =>
                val future = queries.students(studentsRequest)
                onComplete(future) {
                  case Success(value: StudentsResponse.Students) => complete((StatusCodes.OK, value))
                  case Success(StudentsResponse.NotFound())      => complete(StatusCodes.NotFound)
                  case Failure(exception)                        => failWith(exception)
                }
              }
          }
        }
      }
    }

}
