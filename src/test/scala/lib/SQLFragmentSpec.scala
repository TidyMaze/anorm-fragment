package lib

import lib.StringUtils.addDelimiters
import org.scalacheck.Gen.alphaChar
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import org.scalatest.matchers.must.Matchers

object SQLFragmentSpec extends Properties("SQLFragment") with Matchers {

  val runner = new SQLRunner()

  val dataGen = for {
    before <- Gen.alphaStr
    param <- Gen.nonEmptyListOf(alphaChar).map(_.mkString)
    after <- Gen.alphaStr
    replacement <- Gen.alphaStr
  } yield (before, param, after, replacement)

  property("single replace") = forAll(dataGen) {
    case (before: String, param: String, after: String, replacement: String) =>

      val query = SQLFragment(before + addDelimiters(param) + after, Map(
        param -> replacement,
      ))

      val got = runner.run(query)

      val oracle = before + replacement + after

      if (got != oracle) {
        print(got.map(_.toInt))
        print(" vs ")
        print(oracle.map(_.toInt))
        print(" with input ")
        println()
      }

      got == oracle
  }

  val dataGen2 = for {
    before <- Gen.alphaStr
    param <- Gen.nonEmptyListOf(alphaChar).map(_.mkString)
    between <- Gen.alphaStr
    param2 <- Gen.nonEmptyListOf(alphaChar).map(_.mkString)
    after <- Gen.alphaStr
    replacement <- Gen.alphaStr
    replacement2 <- Gen.alphaStr
  } yield (before, param, between, param2, after, replacement, replacement2)

  property("single replace") = forAll(dataGen2) {
    case (before: String, param: String, between: String, param2: String, after: String, replacement: String, replacement2: String) =>

      val query = SQLFragment(before + addDelimiters(param) + between + addDelimiters(param2) + after, Map(
        param -> replacement,
        param2 -> replacement2,
      ))

      val got = runner.run(query)

      val oracle = before + replacement + between + replacement2 + after

      if (got != oracle) {
        print(got.map(_.toInt))
        print(" vs ")
        print(oracle.map(_.toInt))
        print(" with input ")
        println()
      }

      got == oracle
  }
}
