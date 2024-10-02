package hellotest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.slf4j.Logger
import scala.util.matching.Regex
import java.nio.file.{Files, Paths, NoSuchFileException}
import scala.language.unsafeNulls

class LesMisProcessorSpec extends AnyFlatSpec with Matchers with MockitoSugar {
  val logger: Logger = mock[Logger]
  val delimiterPattern: Regex = """\s+""".r
  val minWordLength = 3

  "LesMisProcessor" should "log debug messages for filtered words while processing a file" in {
    val wordCounter = new MockWordCounter(5, 10, Some(2))

    // Create a temporary file with some content
    val tempFilePath = Files.createTempFile("testfile", ".txt")
    Files.write(tempFilePath, "Hello world\nThis is a test line.".getBytes)

    // Create an instance of LesMisProcessor with the mocked logger
    val lesMisProcessor = new LesMisProcessor(delimiterPattern, minWordLength, wordCounter, logger)

    // Call the processFile method
    lesMisProcessor.processFile(tempFilePath.toString)

    // Verify that the logger's debug method was called with the expected messages for words after filtering
    verify(logger).debug(s"Words after filtering: Hello, world")

    // Verify that the wordCounter processed the correct words
    wordCounter.processedWords should contain theSameElementsAs Seq("line.", "test", "this", "world", "hello")

    // Clean up the temporary file
    Files.delete(tempFilePath)
  }

  it should "log an error message when an exception occurs during file processing" in {
    // Simulate a non-existent file path
    val nonExistentFilePath = "non_existent_file.txt"

    // Create an instance of LesMisProcessor with the mocked logger
    val lesMisProcessor = new LesMisProcessor(delimiterPattern, minWordLength, new MockWordCounter(5, 10, Some(2)), logger)

    // Call the processFile method
    lesMisProcessor.processFile(nonExistentFilePath)

    // Verify that the logger's error method was called with an appropriate message
    verify(logger).error(matches("An error occurred while processing the file: .*"))
    
  }
}

// Mock implementation of WordCounter for testing purposes
class MockWordCounter(windowSize: Int, cloudSize: Int, batchSize: Option[Int]) extends WordCounter(windowSize, cloudSize, batchSize) {
  var processedWords: List[String] = Nil

  override def processWord(word: String): Unit = {
    processedWords = word :: processedWords
  }
}
