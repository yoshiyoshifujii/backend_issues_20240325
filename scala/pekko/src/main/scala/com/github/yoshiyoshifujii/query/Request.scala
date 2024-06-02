package com.github.yoshiyoshifujii.query

sealed trait Request

final case class Page(value: Int) {
  require(value > 0)
}
private object Page {
  val default: Page = Page(1)
}

final case class Limit(value: Int) {
  require(value > 0)
}
object Limit {
  val default: Limit = Limit(5)
}

sealed trait Sort
object Sort {
  case object id      extends Sort
  case object name    extends Sort
  case object loginId extends Sort

  val default: Sort = this.id
}

sealed trait Order
object Order {
  case object asc  extends Order
  case object desc extends Order

  val default: Order = this.asc
}

final case class Like(
    name: Option[String],
    loginId: Option[String]
)

final case class StudentsRequest(
    facilitatorId: Int,
    page: Option[Page],
    limit: Option[Limit],
    sort: Option[Sort],
    order: Option[Order],
    like: Option[Like]
) extends Request
