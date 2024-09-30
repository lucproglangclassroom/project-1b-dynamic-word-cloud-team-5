package hellotest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.slf4j.{Logger, LoggerFactory}
import scala.language.unsafeNulls

class MainSpec extends AnyFlatSpec with Matchers {

  // Mock the Logger
  val mockLogger: Logger = mock(classOf[Logger]).nn
  
  // Mock ArgsParser for controlled testing
  object MockArgsParser {
    def parseArgs(args: Array[String]): Option[Config] = {
      // Provide a sample config object for testing
      Some(Config(5, 10)) // Sample windowSize = 5, cloudSize = 10
    }
  }

  "Main" should "parse command line arguments correctly" in {
    // Mock args
    val args = Array("--windowSize", "5", "--cloudSize", "10")

    // Call the main method
    Main.main(args)

    // Verify that the logger was called with the expected message
    verify(mockLogger).info(contains("Parsed command-line arguments"))
  }

}
