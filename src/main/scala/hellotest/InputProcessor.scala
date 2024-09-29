package hellotest

import scala.io.StdIn
import scala.util.control.NonFatal
import scala.util.matching.Regex
import org.log4s.getLogger

// InputProcessor handles input reading and processing using the WordCounter.
class InputProcessor(delimiterPattern: Regex, minWordLength: Int, wordCounter: WordCounter) {
  private val logger = getLogger("InputProcessor")

  def processInput(): Unit = {
    try {
      Iterator.continually(Option(StdIn.readLine()))
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
        logger.info("Input processing interrupted.")
      case NonFatal(e) =>
        logger.error(s"An error occurred during input processing: ${e.getMessage}")
    } finally {
      logger.info("Exiting input processing.")
    }
  }
}