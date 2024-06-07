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

sealed abstract class Sort(value: String) {
  def asString: String = value
}
object Sort {
  case object id      extends Sort("id")
  case object name    extends Sort("name")
  case object loginId extends Sort("login_id")

  def fromString(value: String): Option[Sort] =
    value match {
      case "id"      => Some(id)
      case "name"    => Some(name)
      case "loginId" => Some(loginId)
      case _         => None
    }

  val validValues = "id, name, loginId"

  val default: Sort = this.id
}

sealed abstract class Order(value: String) {
  def asString: String = value
}
object Order {
  case object asc  extends Order("asc")
  case object desc extends Order("desc")

  def fromString(value: String): Option[Order] =
    value match {
      case "asc"  => Some(asc)
      case "desc" => Some(desc)
      case _      => None
    }

  val validValues = "asc, desc"

  val default: Order = this.asc
}

final case class NameLike(value: String) {
  require(1 <= value.length && value.length <= 50)
}
final case class LoginIdLike(value: String) {
  require(1 <= value.length && value.length <= 50)
}

final case class StudentsRequest(
    facilitatorId: Int,
    page: Page,
    limit: Limit,
    sort: Sort,
    order: Order,
    nameLike: Option[NameLike],
    loginIdLike: Option[LoginIdLike]
) extends Request {
  lazy val orderBy: String = s"${sort.asString} ${order.asString}"
  lazy val offset: Int     = limit.value * (page.value - 1)
}
