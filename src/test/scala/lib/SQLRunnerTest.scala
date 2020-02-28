package lib

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class SQLRunnerTest extends AnyFlatSpec with Matchers {
  "cleanup" should "remove crap" in {
    new SQLRunner().removeCrapAroundQuery("SqlQuery(keepThis, timeout = None, fetchSize = None)") mustBe "keepThis"
    new SQLRunner().removeCrapAroundQuery("SqlQuery(\u0000, timeout = None, fetchSize = None)") mustBe "\u0000"
  }
}
