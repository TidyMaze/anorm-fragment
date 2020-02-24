package lib

import java.util.UUID

object StringUtils {
  def replaceMapValues(replacements: Map[String, String], raw: String): String =
    replacements.toList.sortBy {
      case (k,v) => k.length
    }.reverse.foldLeft(raw){
      case (cur, (key, value)) => cur.replace(key, value)
    }

  def replaceMapValuesSafe(replacements: Map[String, String], raw: String): String = {
    val allData = replacements.toList.map {
      case (k,v) => (k,UUID.randomUUID().toString,v)
    }
    val firstMapping = allData.map {
      case (k,i,v) => (k,i)
    }.toMap
    val secondMapping = allData.map {
      case (k,i,v) => (i,v)
    }.toMap

    val step1 = replaceMapValues(
      firstMapping
      , raw
    )

    val step2 = replaceMapValues(
      secondMapping
      , step1
    )

    step2
  }
}
