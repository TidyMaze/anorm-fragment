package lib

import anorm.ParameterValue
import lib.SQLFragment.Params
import lib.StringUtils._

case class SQLFragment(query: String, parameters: Params) {

  def addIf(pred: => Boolean)(that: SQLFragment) = {
    if (pred) {
      this + that
    } else {
      this
    }
  }

  def +?(pred: => Boolean, that: SQLFragment) = addIf(pred)(that)

  def wrap() = SQLFragment(s"(${this.query})", this.parameters)

  def +(that: SQLFragment) = {

    val whatToRenameInOurFragment = SQLFragment.generateDistinctName(that.parameters)(this.parameters)

    val ourQueryRenamed = replaceMapValuesSafe(whatToRenameInOurFragment, query)
    val ourParamsRenamed: Params = parameters.map {
      case (key, value) => (whatToRenameInOurFragment.getOrElse(key, key), value)
    }

    val res = SQLFragment(
      List(ourQueryRenamed.trim, that.query.trim).mkString(" "),
      ourParamsRenamed ++ that.parameters
    )
    println("step " + res + "\n")
    println("param to transform" + parameters)
    println(that.parameters)
    println("and mapping is " + whatToRenameInOurFragment)
    res
  }


}

object SQLFragment {
  type Params = Map[String, ParameterValue]
  type IndexedParameterName = (String, Int)

  val empty = SQLFragment("", Map())

  def generateDistinctName(invariant: Params)(toRename: Params): Map[String, String] = {

    val indexedInvariant: Set[IndexedParameterName] = invariant.map {
      case (name, value) => (name, 0)
    }.toSet
    val indexedToRename: Set[IndexedParameterName] = toRename.map {
      case (name, value) => (name, 0)
    }.toSet

    indexedToRename.map {
      case key@(name, index) if indexedInvariant.contains(key) =>
        (name, serializeName(renameKey(key, indexedInvariant.toSeq)))
      case (name, index) =>
        (name, serializeName(name, index))
    }.toMap
  }

  def serializeName(indexedParameterName: IndexedParameterName) = indexedParameterName match {
    case (name, value) => s"${name}_${value}"
  }

  def renameKey(key: IndexedParameterName, allOtherKeys: Seq[IndexedParameterName]): IndexedParameterName = key match {
    case (name, value) =>
      val oldValue = allOtherKeys.filter {
        case (name2, value2) => name2 == name
      }.map {
        case (name2, value2) => value2
      }
        .maxOption
        .getOrElse(0)
      (name, oldValue + 1)
  }
}
