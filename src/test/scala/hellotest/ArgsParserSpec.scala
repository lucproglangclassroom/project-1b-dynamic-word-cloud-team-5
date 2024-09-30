package hellotest

import hellotest.ArgsParser
import hellotest.Config
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ArgsParserSpec extends AnyFlatSpec with Matchers {

  // Provide implicit CanEqual instances
  given CanEqual[Option[Config], Option[Config]] = CanEqual.derived
  given CanEqual[Option[Config], Some[Config]] = CanEqual.derived
  given CanEqual[Option[Config], None.type] = CanEqual.derived

  "ArgsParser" should "parse default arguments correctly" in {
    val args = Array.empty[String]
    val config = ArgsParser.parseArgs(args)

    config shouldBe Some(Config())
  }

  it should "parse custom cloud size" in {
    val args = Array("--cloud-size", "20")
    val config = ArgsParser.parseArgs(args)

    config shouldBe Some(Config(cloudSize = 20))
  }

  it should "parse custom length at least" in {
    val args = Array("--length-at-least", "8")
    val config = ArgsParser.parseArgs(args)

    config shouldBe Some(Config(lengthAtLeast = 8))
  }

  it should "parse custom window size" in {
    val args = Array("--window-size", "500")
    val config = ArgsParser.parseArgs(args)

    config shouldBe Some(Config(windowSize = 500))
  }

  it should "parse multiple arguments correctly" in {
    val args = Array("--cloud-size", "15", "--length-at-least", "5", "--window-size", "2000")
    val config = ArgsParser.parseArgs(args)

    config shouldBe Some(Config(cloudSize = 15, lengthAtLeast = 5, windowSize = 2000))
  }

  it should "return None for invalid arguments" in {
    val args = Array("--invalid-arg")
    val config = ArgsParser.parseArgs(args)

    config shouldBe None
  }
}
