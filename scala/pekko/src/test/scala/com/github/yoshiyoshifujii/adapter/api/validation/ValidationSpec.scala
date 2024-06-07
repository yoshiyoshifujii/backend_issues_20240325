package com.github.yoshiyoshifujii.adapter.api.validation

import com.github.yoshiyoshifujii.adapter.api.validation.Validation.StudentsParameterValidation
import com.github.yoshiyoshifujii.query._
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec

class ValidationSpec extends AnyFreeSpec with EitherValues {

  "Validation" - {

    "StudentsParameterValidation" - {
      val (facilitatorId, maybePage, maybeLimit, maybeSort, maybeOrder, maybeNameLike, maybeLoginIdLike) = (
        1,
        Some(1),
        Some(5),
        Some("id"),
        Some("asc"),
        None,
        None
      )

      val sut = StudentsParameterValidation

      "正常系" in {
        val actual = sut.validate(
          facilitatorId,
          maybePage,
          maybeLimit,
          maybeSort,
          maybeOrder,
          maybeNameLike,
          maybeLoginIdLike
        )
        assert(
          actual.value === StudentsRequest(
            1,
            Page.default,
            Limit.default,
            Sort.default,
            Order.default,
            None,
            None
          )
        )
      }

      "pageに0を指定できないこと" in {
        val actual = sut.validate(
          facilitatorId,
          Some(0),
          maybeLimit,
          maybeSort,
          maybeOrder,
          maybeNameLike,
          maybeLoginIdLike
        )
        assert(actual.left.value === Invalid("The provided page number must be greater than zero"))
      }

      "limitに0を指定できないこと" in {
        val actual = sut.validate(
          facilitatorId,
          maybePage,
          Some(0),
          maybeSort,
          maybeOrder,
          maybeNameLike,
          maybeLoginIdLike
        )
        assert(actual.left.value === Invalid("The provided limit must be greater than zero"))
      }

      "sortにhogeを指定できないこと" in {
        val actual = sut.validate(
          facilitatorId,
          maybePage,
          maybeLimit,
          Some("hoge"),
          maybeOrder,
          maybeNameLike,
          maybeLoginIdLike
        )
        assert(
          actual.left.value === Invalid(
            "The provided sort parameter is invalid. Allowed values are: id, name, loginId."
          )
        )
      }

      "orderにhogeを指定できないこと" in {
        val actual = sut.validate(
          facilitatorId,
          maybePage,
          maybeLimit,
          maybeSort,
          Some("hoge"),
          maybeNameLike,
          maybeLoginIdLike
        )
        assert(actual.left.value === Invalid("The provided order parameter is invalid. Allowed values are: asc, desc."))
      }

      "name_like" - {
        "0文字を指定できないこと" in {
          val actual = sut.validate(
            facilitatorId,
            maybePage,
            maybeLimit,
            maybeSort,
            maybeOrder,
            Some(""),
            maybeLoginIdLike
          )
          assert(actual.left.value === Invalid("The name_like is too short. Minimum length is 1 characters."))
        }
        "51文字を指定できないこと" in {
          val actual = sut.validate(
            facilitatorId,
            maybePage,
            maybeLimit,
            maybeSort,
            maybeOrder,
            Some("あ" * 51),
            maybeLoginIdLike
          )
          assert(actual.left.value === Invalid("The name_like is too long. Maximum length is 50 characters."))
        }
        "%を含めた文字を指定できないこと" in {
          val actual = sut.validate(
            facilitatorId,
            maybePage,
            maybeLimit,
            maybeSort,
            maybeOrder,
            Some("佐藤%"),
            maybeLoginIdLike
          )
          assert(actual.left.value === Invalid("The name_like contains invalid characters."))
        }
      }

      "loginId_like" - {
        "0文字を指定できないこと" in {
          val actual = sut.validate(
            facilitatorId,
            maybePage,
            maybeLimit,
            maybeSort,
            maybeOrder,
            maybeNameLike,
            Some("")
          )
          assert(actual.left.value === Invalid("The loginId_like is too short. Minimum length is 1 characters."))
        }
        "51文字を指定できないこと" in {
          val actual = sut.validate(
            facilitatorId,
            maybePage,
            maybeLimit,
            maybeSort,
            maybeOrder,
            maybeNameLike,
            Some("a" * 51)
          )
          assert(actual.left.value === Invalid("The loginId_like is too long. Maximum length is 50 characters."))
        }
        "%を含めた文字を指定できないこと" in {
          val actual = sut.validate(
            facilitatorId,
            maybePage,
            maybeLimit,
            maybeSort,
            maybeOrder,
            maybeNameLike,
            Some("hoge%")
          )
          assert(actual.left.value === Invalid("The loginId_like contains invalid characters."))
        }
      }
    }

  }

}
