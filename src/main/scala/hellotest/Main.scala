import scala.io.StdIn
import scala.util.matching.Regex
import scala.collection.mutable

object Main {
  def main(args: Array[String]): Unit = {
    // Validate and parse command-line arguments
    val config = ArgsParser.parseArgs(args).get
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
              // Split the input line into individual words
              val words = delimiterPattern.split(line).filter(_.nonEmpty)
              words.foreach { word =>
                if (word.length >= config.lengthAtLeast) {
                  // Add this to recent words because it is long enough to qualify
                  recentWords.enqueue(word)
                  // Add 1 to its count
                  wordCount(word) = wordCount.getOrElse(word, 0) + 1
                  // Slide window if it has exceeded the max length
                  if (recentWords.size > config.windowSize) {
                    // Take the oldest word out
                    val oldestWord = recentWords.dequeue()
                    // Subtract 1 from its word count since we removed an instance
                    wordCount(oldestWord) -= 1
                    // Delete it from the map if it no longer appears in the window
                    if (wordCount(oldestWord) <= 0) {
                      wordCount.remove(oldestWord)
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
                  }
                }
              }
            }
    }
  }
}
