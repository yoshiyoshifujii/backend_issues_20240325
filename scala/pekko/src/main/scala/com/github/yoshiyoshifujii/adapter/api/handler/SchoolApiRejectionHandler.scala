package com.github.yoshiyoshifujii.adapter.api.handler

import com.github.yoshiyoshifujii.adapter.api.model.json.ErrorResponseJson
import org.apache.pekko.http.scaladsl.model.StatusCodes.BadRequest
import org.apache.pekko.http.scaladsl.server.Directives.complete
import org.apache.pekko.http.scaladsl.server.{
  MalformedQueryParamRejection,
  MissingQueryParamRejection,
  RejectionHandler
}

object SchoolApiRejectionHandler {
  import com.github.yoshiyoshifujii.adapter.api.support.JsonSupport._
  import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case MissingQueryParamRejection(paramName) =>
          complete(
            (
              BadRequest,
              ErrorResponseJson(s"Request is missing required query parameter '$paramName'")
            )
          )
        case MalformedQueryParamRejection(parameterName, errorMsg, cause: Option[Throwable]) =>
          complete(
            (
              BadRequest,
              ErrorResponseJson(
                s"Request query is malformed: '$parameterName' (error: $errorMsg)${cause
                    .map(c => s" (cause: ${c.getMessage})").getOrElse("")}"
              )
            )
          )
      }
      .result()
}
