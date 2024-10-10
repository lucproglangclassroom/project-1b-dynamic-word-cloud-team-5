package hellotest

import scala.collection.immutable.Queue
import org.log4s.getLogger

// WordCounter manages word counts.
trait WordCounterTrait {
  def processWord(word: String, state: CounterState): CounterState
  def printRecentWords(state: CounterState): Unit
}

// A case class to represent the state of the word counter.
case class CounterState(wordCount: Map[String, Int] = Map(), recentWords: Queue[String] = Queue(), processedWords: Int = 0)

class WordCounter(windowSize: Int, cloudSize: Int, batchSize: Option[Int]) extends WordCounterTrait {
  private val logger = getLogger("FunctionalWordCounter")

  // Process a word and return the updated state
  def processWord(word: String, state: CounterState): CounterState = {
    val newRecentWords = state.recentWords.enqueue(word)
    val newWordCount = state.wordCount.updated(word, state.wordCount.getOrElse(word, 0) + 1)
    val newProcessedWords = state.processedWords + 1
    
    // Slide window if exceeded
    val (updatedRecentWords, updatedWordCount) = if (newRecentWords.size > windowSize) {
      val (oldestWord, rest) = newRecentWords.dequeue
      val updatedCount = newWordCount.updatedWith(oldestWord) {
        case Some(count) if count > 1 => Some(count - 1)
        case _ => None
      }
      (rest, updatedCount)
    } else {
      (newRecentWords, newWordCount)
    }

    val updatedState = CounterState(updatedWordCount, updatedRecentWords, newProcessedWords)

    // Print and notify when batch size is met or not defined
    if (updatedRecentWords.size >= windowSize) {
      batchSize match {
        case Some(size) if newProcessedWords >= size =>
          printRecentWords(updatedState)
          updatedState.copy(processedWords = 0)
        case None =>
          printRecentWords(updatedState)
          updatedState
        case _ =>
          updatedState
      }
    } else {
      updatedState
    }
  }

  // Method to print the recentWords queue with word counts, limited to cloudSize
  def printRecentWords(state: CounterState): Unit = {
    // Create a sorted sequence of words with their counts
    val sortedWords = state.wordCount.toSeq
      .sortBy { case (w, count) => (-count, w) }
      .take(cloudSize) // Limit to cloudSize

    val formattedWords = sortedWords.map { case (word, count) => s"$word: $count" }.mkString(" ")

    println(formattedWords)
    logger.debug(s"Recent words (sorted and formatted): $formattedWords")
  }
}
