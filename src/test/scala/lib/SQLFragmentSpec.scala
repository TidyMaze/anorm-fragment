package lib

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import org.scalatest.matchers.must.Matchers

object SQLFragmentSpec extends Properties("SQLFragment") with Matchers {

  val runner = new SQLRunner()

  val dataGen = for {
    before <- Gen.asciiPrintableStr
    param <- Gen.asciiPrintableStr
    after <- Gen.asciiPrintableStr
    replacement <- Gen.asciiPrintableStr
  } yield (before, param, after, replacement)

  property("single replace") = forAll (dataGen) {
    case (before: String, param: String, after: String, replacement: String) =>

    val query = SQLFragment(before + param + after, Map(
      param -> replacement,
    ))

    val got = runner.run(query)
    val oracle = before + replacement + after
    if(got != oracle) {
      println(got.map(_.toInt))
      println("vs")
      println(oracle.map(_.toInt))
    }
    before.contains("\u0000") || param.contains("\u0000") || after.contains("\u0000") || replacement.contains("\u0000") || got == oracle
  }
}
