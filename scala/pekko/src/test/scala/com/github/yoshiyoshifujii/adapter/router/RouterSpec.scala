package com.github.yoshiyoshifujii.adapter.router

import com.github.yoshiyoshifujii.adapter.api.model.json.ErrorResponseJson
import com.github.yoshiyoshifujii.adapter.api.router.Router
import com.github.yoshiyoshifujii.query.StudentsResponse.Students
import com.github.yoshiyoshifujii.query.{ Queries, StudentsRequest }
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class RouterSpec extends AnyFreeSpec with ScalatestRouteTest with MockitoSugar {
  import com.github.yoshiyoshifujii.adapter.api.support.JsonSupport._
  import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  "Router" - {
    val queries = mock[Queries]
    when(queries.students(ArgumentMatchers.any[StudentsRequest]))
      .thenReturn(
        Future.successful(
          Students(
            Vector(),
            1
          )
        )
      )

    val sut = Router(queries)

    "students" - {
      "Ok" in {
        Get("/students?facilitator_id=1") ~> sut.route ~> check {
          assert(
            responseAs[Students] === Students(
              Vector(),
              1
            )
          )
        }
      }
      "Bad Request require" in {
        Get("/students") ~> sut.route ~> check {
          assert(status === StatusCodes.BadRequest)
          assert(
            entityAs[ErrorResponseJson] === ErrorResponseJson(
              "Request is missing required query parameter 'facilitator_id'"
            )
          )
        }
      }
      "Bad Request malformed" in {
        Get("/students?facilitator_id=hoge") ~> sut.route ~> check {
          assert(status === StatusCodes.BadRequest)
          assert(
            entityAs[ErrorResponseJson] === ErrorResponseJson(
              """Request query is malformed: 'facilitator_id' (error: 'hoge' is not a valid 32-bit signed integer value) (cause: For input string: "hoge")"""
            )
          )
        }
      }
    }
  }

}
