package hellotest

import org.slf4j.Logger
import scala.util.control.NonFatal
import scala.util.matching.Regex

// InputProcessor handles input reading and processing using the WordCounter.
trait InputProcessorTrait {
  def processWord(word: String, state: CounterState): CounterState
}

class InputProcessor(delimiterPattern: Regex, minWordLength: Int, wordCounter: WordCounter, inputSource: () => Option[String], logger: Logger) {

  def processInput(): Unit = {
    val initialState = CounterState()
    try {
      Iterator.continually(inputSource())
        .takeWhile(_.isDefined)
        .flatMap(_.toSeq)
        .flatMap { line =>
          logger.debug(s"Processing line: '$line'")
          val words = delimiterPattern.split(line).filter(_.nonEmpty)
          logger.debug(s"Words after filtering: ${words.mkString(", ")}")
          words
        }
        .map(_.toLowerCase)
        .filter(_.nn.length >= minWordLength)
        .scanLeft(initialState) { (state, word) =>
          wordCounter.processWord(word.nn, state)
        }
        .foreach(_ => ())
    } catch {
      case _: InterruptedException =>
        logger.info("Application interrupted (User Interruption Detected). Stopping input processing...")
      case NonFatal(e) =>
        logger.error(s"An error occurred during input processing: ${e.getMessage}")
    } finally {
      logger.info("Exiting input processing.")
    }
  }
}
