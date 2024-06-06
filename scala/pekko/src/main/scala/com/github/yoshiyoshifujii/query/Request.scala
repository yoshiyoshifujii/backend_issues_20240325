package com.github.yoshiyoshifujii.query

sealed trait Request

final case class Page(value: Int) {
  require(value > 0)
}
object Page {
  val default: Page = new Page(1)
}

final case class Limit(value: Int) {
  require(value > 0)
}
object Limit {
  val default: Limit = Limit(5)
}

abstract class Sort(value: String) {
  def asString: String = value
}
object Sort {
  case object id      extends Sort("id")
  case object name    extends Sort("name")
  case object loginId extends Sort("login_id")

  def fromString(value: String): Sort =
    value match {
      case "id"      => id
      case "name"    => name
      case "loginId" => loginId
    }

  val default: Sort = this.id
}

abstract class Order(value: String) {
  def asString: String = value
}
object Order {
  case object asc  extends Order("asc")
  case object desc extends Order("desc")

  def fromString(value: String): Order =
    value match {
      case "asc"  => asc
      case "desc" => desc
    }

  val default: Order = this.asc
}

final case class Like(
    name: Option[String],
    loginId: Option[String]
) {
  require(!(name.nonEmpty && loginId.nonEmpty))
}

final case class StudentsRequest(
    facilitatorId: Int,
    page: Page,
    limit: Limit,
    sort: Sort,
    order: Order,
    like: Option[Like]
) extends Request {
  lazy val orderBy: String = s"${sort.asString} ${order.asString}"
  lazy val offset: Int     = limit.value * (page.value - 1)
}
