package lib
import anorm._

import StringUtils._

class SQLRunner {
  def run(fragment: SQLFragment) = {
    println(s"fragment is $fragment")

    val query = SQL(fragment.query)
      .on(fragment.parameters.map((NamedParameter.apply _).tupled).toSeq: _*)
      .sql

    println(s"query is $query")

    val stringified = new SqlQuery.SqlQueryShow(query).show

    println(s"Stringified is $stringified")

    val replacementMap = fragment.parameters.view.mapValues(_.show).toMap.map{
      case (key, value) => (addDelimiters(key), value)
    }

    println(s"replacementMap is $replacementMap")

    val result = replaceMapValuesSafe(replacementMap, removeCrapAroundQuery(stringified))

    println(s"result is $result")

    result
  }

  def removeCrapAroundQuery(queryOutput: String): String =
    queryOutput.substring("SqlQuery(".length, queryOutput.length - ", timeout = None, fetchSize = None)".length)
}
