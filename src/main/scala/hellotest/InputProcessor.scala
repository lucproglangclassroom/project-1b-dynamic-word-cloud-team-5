package hellotest

import org.slf4j.Logger
import scala.util.control.NonFatal
import scala.util.matching.Regex

// InputProcessor handles input reading and processing using the WordCounter.
class InputProcessor(delimiterPattern: Regex, minWordLength: Int, wordCounter: WordCounter, inputSource: () => Option[String], logger: Logger) {

  def processInput(): Unit = {
    try {
      Iterator.continually(inputSource())
        .takeWhile(_.isDefined)
        .foreach { lineOpt =>
          lineOpt.foreach { line =>
            logger.debug(s"Processing line: '$line'")
            val words = delimiterPattern.split(line).filter(_.nonEmpty)
            logger.debug(s"Words after filtering: ${words.mkString(", ")}")

            words.foreach { word =>
              val normalizedWord = word.toLowerCase.nn
              if (normalizedWord.length >= minWordLength) {
                wordCounter.processWord(normalizedWord)
              }
            }
          }
        }
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
