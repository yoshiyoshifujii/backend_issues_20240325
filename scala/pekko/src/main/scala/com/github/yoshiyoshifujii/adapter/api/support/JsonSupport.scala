package com.github.yoshiyoshifujii.adapter.api.support

import com.github.yoshiyoshifujii.adapter.api.model.json.ErrorResponseJson
import com.github.yoshiyoshifujii.query.StudentsResponse.{ ClassRoom, Student, Students }
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

object JsonSupport {

  implicit val errorResponseJsonFormat: RootJsonFormat[ErrorResponseJson] = jsonFormat1(ErrorResponseJson)
  implicit val classRoomJsonFormat: RootJsonFormat[ClassRoom]             = jsonFormat2(ClassRoom)
  implicit val studentJsonFormat: RootJsonFormat[Student]                 = jsonFormat4(Student)
  implicit val studentsJsonFormat: RootJsonFormat[Students]               = jsonFormat2(Students)

}
