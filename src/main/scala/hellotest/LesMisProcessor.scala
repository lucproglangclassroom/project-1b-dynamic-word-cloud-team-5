package hellotest

import scala.util.control.NonFatal
import scala.io.Source
import scala.util.matching.Regex
import org.slf4j.Logger

class LesMisProcessor(
  delimiterPattern: Regex,
  minWordLength: Int,
  wordCounter: WordCounter,
  logger: Logger
) {
  def processFile(filePath: String): Unit = {
    try {
      val source = Source.fromFile(filePath)
      for (line <- source.getLines()) {
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
      source.close()
    } catch {
      case NonFatal(e) =>
        logger.error(s"An error occurred while processing the file: ${e.getMessage}")
    } finally {
      logger.info("Finished processing the file.")
    }
  }
}
