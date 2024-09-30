package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.matching.Regex
import java.nio.file.{Files, Paths}
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.slf4j.{Logger, LoggerFactory}
import scala.language.unsafeNulls

// Mock implementation of WordCounter for testing purposes
class MockWordCounter(windowSize: Int, cloudSize: Int, batchSize: Int) extends WordCounter(windowSize, cloudSize, batchSize) {
  var processedWords: List[String] = Nil

  override def processWord(word: String): Unit = {
    processedWords = word :: processedWords
  }
}

class LesMisProcessorSpec extends AnyFlatSpec with Matchers {
  val logger: Logger = mock(classOf[Logger]).nn
  val delimiterPattern: Regex = """\s+""".r
  val minWordLength = 3

  it should "not process any words if the file is empty" in {
    val wordCounter = new MockWordCounter(5, 10, 2)
    var infoLogged = false

    val infoLogger: Logger = mock(classOf[Logger]).nn

    when(infoLogger.info(any[String])).thenAnswer { invocation =>
      val message = invocation.getArgument [String](0)
      infoLogged = true
      message.contains("Finished processing the file.")
    }

    // Create a temporary empty file
    val tempFilePath = Files.createTempFile("emptyfile", ".txt")
    val lesMisProcessor = new LesMisProcessor(delimiterPattern, minWordLength, wordCounter, infoLogger)
    lesMisProcessor.processFile(tempFilePath.toString)

    // Assert that info was logged
    infoLogged shouldBe true

    // Clean up the temporary file
    Files.delete(tempFilePath)
  }
}
