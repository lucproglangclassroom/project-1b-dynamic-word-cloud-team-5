package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import scala.util.matching.Regex
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ListBuffer
import org.slf4j.LoggerFactory
import org.slf4j.Logger



class LesMisProcessorSpec extends AnyFlatSpec with Matchers with MockitoSugar{

  val errorLogger: Logger = mock[Logger]

  val delimiterPattern: Regex = """\s+""".r
  val minWordLength = 3
  val logger: Logger = LoggerFactory.getLogger(classOf[LesMisProcessorSpec]).nn

  "LesMisProcessor" should "process and log filtered words while processing a file" in {
    // Initial state for the word counter
    var state = CounterState()

    // Word counter function using the provided WordCounter class
    val wordCounter = new WordCounter(windowSize = 10, cloudSize = 5, batchSize = None)

    // Create a temporary file with some content
    val tempFilePath = Files.createTempFile("testfile", ".txt")
    Files.write(tempFilePath, "Hello world\nThis is a test line.".getBytes)

    // Create an instance of LesMisProcessor
    val processor = new LesMisProcessor(
      delimiterPattern,
      minWordLength,
      (word, currentState) => wordCounter.processWord(word, currentState),
      errorLogger
    )

    // Call the processFile method
    processor.processFile(tempFilePath.toString) match {
      case Right(_) => succeed
      case Left(_)  => fail("Processing file should not fail.")
    }

    // Check the final state after processing
    state.wordCount.keySet should contain allElementsOf Seq("line", "test", "this", "world", "hello")

    // Clean up the temporary file
    Files.delete(tempFilePath)
  }

  it should "log an error message when an exception occurs during file processing" in {
    // ListBuffer to collect errors logged (functional replacement instead of using the mocks)
    val errorMessages = ListBuffer.empty[String]

    // Simulate a non-existent file path
    val nonExistentFilePath = "non_existent_file.txt"

    // Create an instance of LesMisProcessor
    val processor = new LesMisProcessor(delimiterPattern, minWordLength, (word, currentState) => currentState, errorLogger)

    // Call the processFile method
    processor.processFile(nonExistentFilePath) match {
      case Left(_) => succeed
      case Right(_) => fail("Processing non-existent file should fail.")
    }

    // Verify that the error logger captured the expected message
    errorMessages.exists(_.contains("An error occurred while processing the file")) should be(true)
  }
}
