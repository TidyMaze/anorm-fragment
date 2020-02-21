import lib.{SQLFragment, SQLRunner}

object Demo extends App {
  val population: Int = 2000

  val fragment1 = SQLFragment("select * from Country c join CountryLanguage l on l.CountryCode = c.Code where l.Language = {param}", Map(
    "param" -> "fr",
  ))

  val fragment2 = SQLFragment("and c.Population >= {param}", Map(
    "param" -> population
    ))

  val orderFragment = SQLFragment("order by c.Population desc limit 1", Map())

  val query = fragment1.addIf(population > 0){fragment2} +? (false, fragment1) + orderFragment

  val runner = new SQLRunner()

  println(runner.run(query))
}
