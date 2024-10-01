package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.slf4j.{Logger, LoggerFactory}
import scala.language.unsafeNulls

// Mock object for ArgsParser
object MockArgsParser {
  def parseArgs(args: Array[String]): Option[Config] = {
    // Different behaviors based on the arguments passed for testing
    args.toList match {
      case List("--windowSize", "5", "--cloudSize", "10") =>
        Some(Config(10, 6, 5, 100)) // Valid case
      case List("--windowSize", "invalid") =>
        None // Simulate invalid argument
      case _ =>
        None // Simulate other invalid cases
    }
  }
}


class MainSpec extends AnyFlatSpec with Matchers {

  // Mock the Logger
  val logger: Logger = mock(classOf[Logger]).nn

  
  Main.logger = logger

  "Main" should "parse valid command line arguments correctly" in {
    // Valid arguments
    val args = Array("--windowSize", "5", "--cloudSize", "10")

  }
}
