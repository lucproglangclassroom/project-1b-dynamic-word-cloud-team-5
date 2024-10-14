package hellotest

import scala.io.Source
import scala.util.control.NonFatal
import scala.util.matching.Regex
import org.slf4j.Logger

class LesMisProcessor(
    delimiterPattern: Regex,
    minWordLength: Int,
    wordCounter: (String, CounterState) => CounterState, 
    logger: Logger
) {

  def processFile(filePath: String): Either[Throwable, Unit] = {
    try {
      val source = Source.fromFile(filePath)
      // wordCounter initial state
      val initialState = CounterState() 
      
      // Processing the file lines in a functionally
      val finalState = source.getLines().foldLeft(initialState) { (state, line) =>
        logger.debug(s"Processing line: '$line'")
        val words = delimiterPattern.split(line).filter(_.nonEmpty).map(_.toLowerCase.nn)
        
        logger.debug(s"Words after filtering: ${words.mkString(", ")}")
        words.foldLeft(state) { (currentState, word) =>
          if (word.length >= minWordLength) {
            wordCounter(word, currentState) 
          } else {
            currentState
          }
        }
      }

      source.close()
      // Return success
      Right(()) 

    } catch {
      case NonFatal(e) =>
        logger.error(s"An error occurred while processing the file: ${e.getMessage}")
        // Return the exception
        Left(e) 
    } finally {
      logger.info("Finished processing the file.")
    }
  }
}



