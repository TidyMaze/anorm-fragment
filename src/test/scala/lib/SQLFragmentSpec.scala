package lib

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import org.scalatest.matchers.must.Matchers

object SQLFragmentSpec extends Properties("SQLFragment") with Matchers {

  val runner = new SQLRunner()

  val dataGen = for {
    before <- Gen.alphaStr
    param <- Gen.alphaStr
    after <- Gen.alphaStr
    replacement <- Gen.alphaStr
  } yield (before, param, after, replacement)

  property("single replace") = forAll(dataGen) {
    case (before: String, param: String, after: String, replacement: String) =>

      val query = SQLFragment(before + param + after, Map(
        param -> replacement,
      ))

      val got = runner.run(query)
      val oracle = before + replacement + after
      if (got != oracle) {
        print(got.map(_.toInt))
        print(" vs ")
        print(oracle.map(_.toInt))
        print(" with input ")
        print((before.map(_.toInt), param.map(_.toInt), after.map(_.toInt), replacement.map(_.toInt)))

        println()
      }

      Seq(before, param, after, replacement).exists(_.contains("\u0000")) || got == oracle
  }
}
