package hellotest

import scala.collection.mutable
import org.log4s.getLogger

// WordCounter manages word counts and notifies observers.
class WordCounter(windowSize: Int, cloudSize: Int, batchSize: Int) {
  private val logger = getLogger("WordCounter")
  private val wordCount = mutable.Map[String, Int]()
  private val recentWords = mutable.Queue[String]()
  private var observers: List[WordCountObserver] = List()
  private var processedWords = 0

  def addObserver(observer: WordCountObserver): Unit = {
    observers = observer :: observers
  }

  private def notifyObservers(): Unit = {
    val sortedWords = wordCount.toSeq
      .sortBy { case (w, count) => (-count, w) }
      .take(cloudSize)

    sortedWords.foreach { case (word, count) =>
      observers.foreach(_.updateWordCount(word, count))
    }
  }

  def processWord(word: String): Unit = {
  recentWords.enqueue(word)
  wordCount(word) = wordCount.getOrElse(word, 0) + 1
  logger.debug(s"Added word: '$word', new count: ${wordCount(word)}")
  processedWords += 1 // Increment processedWords

  // Slide window if exceeded
  if (recentWords.size > windowSize) {
    val oldestWord = recentWords.dequeue()
    wordCount(oldestWord) -= 1
    logger.debug(s"Removed oldest word: '$oldestWord', new count: ${wordCount.getOrElse(oldestWord, 0)}")

    if (wordCount(oldestWord) <= 0) {
      wordCount.remove(oldestWord)
      logger.debug(s"Removed '$oldestWord' from word count map")
    }
  }

  // Print and notify observers after processing a batch of words
  if (processedWords >= batchSize) {
    printRecentWords()
    notifyObservers()
    processedWords = 0 // Reset the counter
  }
}

  // Method to print the recentWords queue with word counts, limited to cloudSize
  private def printRecentWords(): Unit = {
    // Create a sorted sequence of words with their counts
    val sortedWords = wordCount.toSeq
      .sortBy { case (w, count) => (-count, w) }
      .take(cloudSize) // Limit to cloudSize

    // Format and print the words in the required format: "word: count"
    val formattedWords = sortedWords.map { case (word, count) => s"$word: $count" }.mkString(" ")


    // Print to console
    println(formattedWords)
    logger.debug(s"Recent words (sorted and formatted): $formattedWords")
  }
}
