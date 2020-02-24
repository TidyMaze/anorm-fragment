package lib
import anorm._

import StringUtils._

class SQLRunner {
  def run(fragment: SQLFragment) = {
    val query = SQL(fragment.query)
      .on(fragment.parameters.map((NamedParameter.apply _).tupled).toSeq: _*)
      .sql
    val stringified = new SqlQuery.SqlQueryShow(query).show
    replaceMapValuesSafe(fragment.parameters.view.mapValues(_.show).toMap, removeCrapAroundQuery(stringified))
  }

  def removeCrapAroundQuery(queryOutput: String): String =
    queryOutput.substring("SqlQuery(".length, queryOutput.length - ", timeout = None, fetchSize = None)".length)
}
