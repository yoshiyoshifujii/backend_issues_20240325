package com.github.yoshiyoshifujii.adapter.api.validation

import com.github.yoshiyoshifujii.query._

case class Invalid(message: String)

trait Validation[A, B] {

  def validate(input: A): Either[Vector[Invalid], B]

}

object Validation {

  private type StudentsParameter =
    (Int, Option[Int], Option[Int], Option[String], Option[String], Option[String], Option[String])

  object StudentsParameterValidation extends Validation[StudentsParameter, StudentsRequest] {
    override def validate(input: StudentsParameter): Either[Vector[Invalid], StudentsRequest] =
      input match {
        case (_, _, _, _, _, Some(_), Some(_)) =>
          Left(Vector(Invalid("Either name_like or loginId_like must be specified")))
        case (facilitatorId, maybePage, maybeLimit, maybeSort, maybeOrder, maybeNameLike, maybeLoginIdLike) =>
          Right(
            StudentsRequest(
              facilitatorId = facilitatorId,
              page = maybePage.map(Page.apply).getOrElse(Page.default),
              limit = maybeLimit.map(Limit.apply).getOrElse(Limit.default),
              sort = maybeSort.map(Sort.fromString).getOrElse(Sort.default),
              order = maybeOrder.map(Order.fromString).getOrElse(Order.default),
              like = maybeNameLike.map(n => Like(Some(n), None)).orElse(maybeLoginIdLike.map(l => Like(None, Some(l))))
            )
          )
      }
  }

}
