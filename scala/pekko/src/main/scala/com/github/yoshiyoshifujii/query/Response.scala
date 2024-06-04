package com.github.yoshiyoshifujii.query

sealed trait Response

sealed trait StudentsResponse extends Response
object StudentsResponse {

  final case class ClassRoom(
      id: Int,
      name: String
  )

  final case class Student(
      id: Int,
      name: String,
      loginId: String,
      classroom: ClassRoom
  )

  final case class Students(
      students: Vector[Student],
      totalCount: Int
  ) extends StudentsResponse

  final case class NotFound() extends StudentsResponse

}
