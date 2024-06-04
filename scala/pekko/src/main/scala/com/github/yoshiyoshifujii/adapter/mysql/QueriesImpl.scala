package com.github.yoshiyoshifujii.adapter.mysql

import cats.effect.IO
import cats.effect.unsafe.implicits._
import cats.free.Free
import com.github.yoshiyoshifujii.query.{ Queries, StudentsRequest, StudentsResponse }
import doobie.free.connection
import doobie.implicits._
import doobie.util.transactor.Transactor

import scala.concurrent.Future

private final case class CountDTO(
    cnt: Int
)

private final case class StudentDTO(
    id: Int,
    name: String,
    login_id: String,
    class_id: Int,
    class_name: String,
    facilitator_id: Int
)

class QueriesImpl(transactor: Transactor[IO]) extends Queries {

  override def students(request: StudentsRequest): Future[StudentsResponse] = {
    def findStudents: Free[connection.ConnectionOp, Vector[StudentsResponse.Student]] =
      sql"""
         select
           s.id as id,
           s.name as name,
           s.login_id as login_id,
           c.id as class_id,
           c.name as class_name,
           s.facilitator_id as facilitator_id
         from students s
         inner join classes c on s.class_id = c.id
         where
           s.facilitator_id = ${request.facilitatorId}
         limit ${request.limit.value}
       """
        .query[StudentDTO]
        .to[Vector]
        .map(_.map { dto =>
          StudentsResponse.Student(
            dto.id,
            dto.name,
            dto.login_id,
            StudentsResponse.ClassRoom(dto.class_id, dto.class_name)
          )
        })

    def count: Free[connection.ConnectionOp, Int] =
      sql"""
         select
           count(s.id) as cnt
         from students s
         inner join classes c on s.class_id = c.id
         where
           s.facilitator_id = ${request.facilitatorId}
         """
        .query[CountDTO]
        .unique
        .map(_.cnt)

    (for {
      students   <- findStudents
      totalCount <- count
    } yield StudentsResponse.Students(students, totalCount))
      .transact(transactor)
      .unsafeToFuture()
  }
}
