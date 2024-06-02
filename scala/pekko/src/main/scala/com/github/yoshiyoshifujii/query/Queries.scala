package com.github.yoshiyoshifujii.query

import scala.concurrent.Future

trait Queries {

  def students(request: StudentsRequest): Future[StudentResponse]

}
