package com.github.yoshiyoshifujii.adapter.api.validation

import org.apache.pekko.http.scaladsl.server.Directives.reject
import org.apache.pekko.http.scaladsl.server.{ Directive, Directive1, Directives }

object ValidationDirectives {

  def convertWithValidation[A, B](validation: Validation[A, B])(value: A): Directive1[B] =
    Directives.provide(value).flatMap { v =>
      Directive[Tuple1[B]] { inner =>
        validation.validate(v) match {
          case Right(b)  => inner(Tuple1(b))
          case Left(inv) => reject(ValidationRejection(inv))
        }
      }
    }
}
