package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.matching.Regex
import org.slf4j.{Logger, LoggerFactory}

class MainSpec extends AnyFlatSpec with Matchers {

  val logger: Logger = LoggerFactory.getLogger("Dynamic Word Cloud").nn

  "Main" should "run with simulated input without errors" in {
    // Simulated input 
    val simulatedInput = List("hello world", "scala rocks", "functional programming", "exit").iterator
    val inputSource: () => Option[String] = () => if (simulatedInput.hasNext) Some(simulatedInput.next()) else None

    // Set up and run the main application with simulated input
    val args = Array("--window-size", "5", "--cloud-size", "10")
    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r
    val wordCounter = new WordCounter(5, 10, None)
    val inputProcessor = new InputProcessor(delimiterPattern, 6, wordCounter, inputSource, logger)

    noException should be thrownBy {
      inputProcessor.processInput() // Process the simulated input
    }
  }

  it should "handle invalid arguments gracefully without throwing an exception" in {
    // Simulate invalid command-line arguments
    val args = Array("--window-size", "invalid")

    // Test the main function's handling of invalid arguments
    noException should be thrownBy {
      Main.main(args)
    }
  }
}


