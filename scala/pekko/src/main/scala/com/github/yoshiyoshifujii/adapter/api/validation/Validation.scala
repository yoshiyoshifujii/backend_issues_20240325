package com.github.yoshiyoshifujii.adapter.api.validation

import com.github.yoshiyoshifujii.query._

import scala.util.matching.Regex

case class Invalid(message: String)

trait Validation[A, B] {

  def validate(input: A): Either[Invalid, B]

}

object Validation {

  private type StudentsParameter =
    (Int, Option[Int], Option[Int], Option[String], Option[String], Option[String], Option[String])

  object StudentsParameterValidation extends Validation[StudentsParameter, StudentsRequest] {
    override def validate(input: StudentsParameter): Either[Invalid, StudentsRequest] = {
      val (facilitatorId, maybePage, maybeLimit, maybeSort, maybeOrder, maybeNameLike, maybeLoginIdLike) = input
      for {
        page        <- validatePage(maybePage)
        limit       <- validateLimit(maybeLimit)
        sort        <- validateSort(maybeSort)
        order       <- validateOrder(maybeOrder)
        nameLike    <- validateNameLike(maybeNameLike)
        loginIdLike <- validateLoginIdLike(maybeLoginIdLike)
      } yield StudentsRequest(
        facilitatorId = facilitatorId,
        page = page,
        limit = limit,
        sort = sort,
        order = order,
        nameLike = nameLike,
        loginIdLike = loginIdLike
      )
    }

    private def validateGtZero[T](
        maybeValue: Option[Int],
        default: T,
        create: Int => T,
        errorMsg: String
    ): Either[Invalid, T] =
      maybeValue match {
        case None             => Right(default)
        case Some(v) if v > 0 => Right(create(v))
        case _                => Left(Invalid(errorMsg))
      }

    private def validatePage(maybePage: Option[Int]): Either[Invalid, Page] =
      validateGtZero(maybePage, Page.default, Page.apply, "The provided page number must be greater than zero")

    private def validateLimit(maybeLimit: Option[Int]): Either[Invalid, Limit] =
      validateGtZero(maybeLimit, Limit.default, Limit.apply, "The provided limit must be greater than zero")

    private def validateEnumString[T](
        maybeValue: Option[String],
        default: T,
        fromString: String => Option[T],
        errorMsg: String
    ): Either[Invalid, T] =
      maybeValue match {
        case None        => Right(default)
        case Some(value) => fromString(value).toRight(Invalid(errorMsg))
      }

    private def validateSort(maybeSort: Option[String]): Either[Invalid, Sort] =
      validateEnumString(
        maybeSort,
        Sort.default,
        Sort.fromString,
        s"The provided sort parameter is invalid. Allowed values are: ${Sort.validValues}."
      )

    private def validateOrder(maybeOrder: Option[String]): Either[Invalid, Order] =
      validateEnumString(
        maybeOrder,
        Order.default,
        Order.fromString,
        s"The provided order parameter is invalid. Allowed values are: ${Order.validValues}."
      )

    private def validateLikeSearch[T](
        maybeLike: Option[String],
        minLength: Int,
        maxLength: Int,
        allowedPattern: Regex,
        apply: String => T,
        keyName: String
    ): Either[Invalid, Option[T]] = {
      maybeLike match {
        case None => Right(None)
        case Some(v) if v.length < minLength =>
          Left(Invalid(s"The $keyName is too short. Minimum length is $minLength characters."))
        case Some(v) if maxLength < v.length =>
          Left(Invalid(s"The $keyName is too long. Maximum length is $maxLength characters."))
        case Some(v) if !allowedPattern.matches(v) =>
          Left(Invalid(s"The $keyName contains invalid characters."))
        case Some(v) => Right(Some(apply(v)))
      }
    }

    private def validateNameLike(maybeNameLike: Option[String]): Either[Invalid, Option[NameLike]] =
      validateLikeSearch(
        maybeNameLike,
        minLength = 1,
        maxLength = 50,
        allowedPattern = """^[\p{L}\s]+$""".r,
        apply = NameLike.apply,
        keyName = "name_like"
      )

    private def validateLoginIdLike(maybeLoginIdLike: Option[String]): Either[Invalid, Option[LoginIdLike]] =
      validateLikeSearch(
        maybeLoginIdLike,
        minLength = 1,
        maxLength = 50,
        allowedPattern = """^[a-zA-Z0-9_.#:-]+$""".r,
        apply = LoginIdLike.apply,
        keyName = "loginId_like"
      )
  }

}
