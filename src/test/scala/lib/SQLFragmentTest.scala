package lib

import lib.SQLFragment.Params
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class SQLFragmentTest extends AnyFlatSpec with Matchers {
  val population: Int = 2000

  "An SQLFragment" should "replace params" in {
    val fragment1 = SQLFragment("select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {lang}", Map(
      "lang" -> "fr",
    ))

    val fragment2 = SQLFragment("and c.Population >= {population}", Map(
      "population" -> population
    ))

    val orderFragment = SQLFragment("order by c.Population desc limit 1", Map.empty)

    val query = fragment1 + fragment2 + orderFragment

    val runner = new SQLRunner()

    runner.run(query) mustBe "SqlQuery(select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {fr} and c.Population >= {2000} order by c.Population desc limit 1, timeout = None, fetchSize = None)"
  }

  "An SQLFragment" should "replace params with same name in different fragments" in {
    val fragment1 = SQLFragment("select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {param}", Map(
      "param" -> "fr",
    ))

    val fragment2 = SQLFragment("and c.Population >= {param}", Map(
      "param" -> population
    ))

    val orderFragment = SQLFragment("order by c.Population desc limit {param}", Map(
      "param" -> 10
    ))


    val query = (fragment1 + fragment2) + orderFragment

    println(query.query)
    println("query.parameters" + query.parameters)

    val runner = new SQLRunner()

    val res = runner.run(query)

    println("res:" + res)

    //query.parameters mustBe Map[String, ParameterValue]("param" -> 10, "param_1" -> population, "param_1_1" -> "fr")

    res mustBe "SqlQuery(select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {fr} and c.Population >= {2000} order by c.Population desc limit {10}, timeout = None, fetchSize = None)"
  }

  "An SQLFragment" should "replace params with different names" in {
    val fragment1 = SQLFragment("select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {lang}", Map(
      "lang" -> "fr",
    ))

    val fragment2 = SQLFragment("and c.Population >= {population}", Map(
      "population" -> population
    ))

    val orderFragment = SQLFragment("order by c.Population desc limit {lol}", Map(
      "lol" -> 10
    ))

    val query = fragment1 + fragment2 + orderFragment

    val runner = new SQLRunner()

    val res = runner.run(query)

    println(res)

    //query.parameters mustBe Map[String, ParameterValue]("lol" -> 10, "population" -> population, "lang" -> "fr")

    res mustBe "SqlQuery(select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {fr} and c.Population >= {2000} order by c.Population desc limit {10}, timeout = None, fetchSize = None)"
  }

  "generateDistinctName" should "build a correct map with same name params" in {
    val invariant: Params = Map("toto" -> 880)
    val toRename: Params = Map("toto" -> 30)

    val oracle: Map[String, String] = Map("toto" -> "toto_1")

    SQLFragment.generateDistinctName(invariant)(toRename) mustBe oracle
  }
}
