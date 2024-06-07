package com.github.yoshiyoshifujii.adapter.api.validation

import org.apache.pekko.http.scaladsl.server.Rejection

case class ValidationRejection(message: Invalid) extends Rejection
