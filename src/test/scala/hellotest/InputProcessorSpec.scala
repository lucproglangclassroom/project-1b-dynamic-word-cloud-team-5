package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.{any, anyString, contains}
import org.scalatest.BeforeAndAfter
import org.slf4j.{Logger, LoggerFactory}

import scala.language.unsafeNulls
import scala.util.matching.Regex

class InputProcessorSpec extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfter {
  // Initial and static variables
  val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r
  val minWordLength = 6
  var inputLines: List[String] = _
  var iterator: Iterator[String] = _
  var logger: Logger = _
  var wordCounter: WordCounter = _


  before {
    inputLines = List("This is a test line", "Another line for testing")
    iterator = inputLines.iterator
    logger = mock[Logger]
    wordCounter = mock[WordCounter]
  }
  // Function to return the next input line as Option[String]
  val inputSource: () => Option[String] = () => {
    if (iterator.hasNext) Some(iterator.next()) else None
  }

  it should "process input lines correctly" in {
    val inputProcessor = new InputProcessor(delimiterPattern, minWordLength, wordCounter, inputSource, logger)

    when(wordCounter.nn.processWord(anyString(), any(classOf[CounterState]))).thenReturn(CounterState())

    // Call the method to be tested
    inputProcessor.processInput()

    // Verify that the processWord method is called with the correct words
    verify(wordCounter, times(1)).nn.processWord(("another"), any(classOf[CounterState]))
    verify(wordCounter, times(1)).nn.processWord(("testing"), any(classOf[CounterState]))
    verify(wordCounter, times(0)).nn.processWord(("this"), any(classOf[CounterState]))
    verify(wordCounter, times(0)).nn.processWord(("is"), any(classOf[CounterState]))
    verify(wordCounter, times(0)).nn.processWord(("test"), any(classOf[CounterState]))
    verify(wordCounter, times(0)).nn.processWord(("line"), any(classOf[CounterState]))
    verify(logger, times(2)).debug(contains("Processing line:"))
    verify(logger, times(2)).debug(contains("Words after filtering:"))
  }
  it should "log info when runtime error received" in {
    val inputProcessor = new InputProcessor(delimiterPattern, minWordLength, wordCounter, inputSource, logger)
    doAnswer(invocation => {
      throw new RuntimeException("Simulated Error")
    }).when(wordCounter).nn.processWord((anyString()), any(classOf[CounterState]))

    // Process the input
    inputProcessor.processInput()

    // Verify that the logger was called with the expected message
    verify(logger).nn.error(contains("An error occurred during input processing"))
    verify(logger).nn.info(contains("Exiting input processing."))
  }
  it should "log info when interrupted" in {
    // Create a mock logger as a field
    val logger: Logger = mock[Logger]
    // Create mock wordCounter
    val wordCounter: WordCounter = mock[WordCounter]
    val inputProcessor = new InputProcessor(delimiterPattern, minWordLength, wordCounter, inputSource, logger)
    doAnswer(invocation => {
      throw new InterruptedException("Simulated Interruption")
    }).when(wordCounter).nn.processWord((anyString()), any(classOf[CounterState]))

    // Process the input
    inputProcessor.processInput()

    // Verify if the logger was called with the expected message
    verify(logger).info(contains("Application interrupted (User Interruption Detected). Stopping input processing..."))
    verify(logger).info(contains("Exiting input processing."))

  }
}
