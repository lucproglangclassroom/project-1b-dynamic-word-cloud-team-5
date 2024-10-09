package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.language.unsafeNulls

class MainSpec extends AnyFlatSpec with Matchers {

  "Main" should "parse valid command line arguments correctly and run without errors" in {
    // Simulate valid command-line arguments
    val args = Array("--window-size", "5", "--cloud-size", "10")

    // Test the main function without modifying the logger
    noException should be thrownBy {
      Main.main(args)
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


