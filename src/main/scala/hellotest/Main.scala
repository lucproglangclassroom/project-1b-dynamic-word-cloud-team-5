import scala.io.StdIn
import scala.util.matching.Regex
import scala.collection.mutable

object Main {
  private val logger = org.log4s.getLogger("Main")

  def main(args: Array[String]): Unit = {
    // Validate and parse command-line arguments
    val config = ArgsParser.parseArgs(args).get
    // Logs parsed arguments
    logger.info(s"Parsed command-line arguments: $config")

    // Regular expression to match non-alphanumeric characters
    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r

        // Keeps track of words and their counts
        val wordCount = mutable.Map[String, Int]()
        // This will be the sliding window
        val recentWords = mutable.Queue[String]()

        // Process input lines
        Iterator.continually(Option(StdIn.readLine()))
          .takeWhile(_.isDefined)
          .foreach { lineOpt =>
            lineOpt.foreach { line =>
              // Logs each line being processed from standard input
              logger.debug(s"Processing line: '$line'")  

              // Split the input line into individual words
              val words = delimiterPattern.split(line).filter(_.nonEmpty)
              // Logs words after they're filtered with delimiterPattern
              logger.debug(s"Words after filtering: ${words.mkString(", ")}")  

              words.foreach { word =>
                val normalizedWord = word.toLowerCase.nn
                if (normalizedWord.length >= config.lengthAtLeast) {
                  // Add this to recent words because it is long enough to qualify
                  recentWords.enqueue(normalizedWord)
                  // Add 1 to its count
                  wordCount(normalizedWord) = wordCount.getOrElse(normalizedWord, 0) + 1
                  // Logs an added word
                  logger.debug(s"Added word: '$normalizedWord', new count: ${wordCount(normalizedWord)}")

                  // Slide window if it has exceeded the max length
                  if (recentWords.size > config.windowSize) {
                    // Take the oldest word out
                    val oldestWord = recentWords.dequeue()
                    // Subtract 1 from its word count since we removed an instance
                    wordCount(oldestWord) -= 1
                    // Logs a removed oldest word
                    logger.debug(s"Removed oldest word: '$oldestWord', new count: ${wordCount.getOrElse(oldestWord, 0)}")

                    // Delete it from the map if it no longer appears in the window
                    if (wordCount(oldestWord) <= 0) {
                      wordCount.remove(oldestWord)
                      // Logs removal from map
                      logger.debug(s"Removed '$oldestWord' from word count map")
                    }
                  }

                  if (recentWords.size >= config.windowSize) {
                    // Sort the map by descending count and limit to the cloud size
                    val sortedWords = wordCount.toSeq
                      .sortBy { case (w, count) => (-count, w) }
                      .take(config.cloudSize) // Limit to specified cloud size
                    // Print in the format suggested by the prompt
                    val output = sortedWords.map { case (w, count) => s"$w: $count" }.mkString(" ")
                    println(output)
                    // Logs output
                    logger.info(s"Generated output: $output")
                  }
                }
              }
            }
    }
  }
}
