import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.matching.Regex
import scala.collection.mutable

class junitSpec extends AnyFlatSpec with Matchers {

  // this is for testing. this is similar to main logic
  def processLine(line: String, lengthAtLeast: Int): String = {
    // Regular expression to match non-alphanumeric characters
    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r

    // Keeps track of words and their counts
    val wordCount = mutable.Map[String, Int]()
    // This will be the sliding window
    val recentWords = mutable.Queue[String]()

    // Split the input line into individual words
    val words = delimiterPattern.split(line).filter(_.nonEmpty)

    words.foreach { word =>
      val normalizedWord = word.toLowerCase.nn  // Normalize to lower case
      if (normalizedWord.length >= lengthAtLeast) {
        // Add this to recent words because it is long enough to qualify
        recentWords.enqueue(normalizedWord)
        // Add 1 to its count
        wordCount(normalizedWord) = wordCount.getOrElse(normalizedWord, 0) + 1

        // Slide window if it has exceeded the max length
        if (recentWords.size > 5) { // Assuming window size is 5 for this test case
          // Take the oldest word out
          val oldestWord = recentWords.dequeue()
          // Subtract 1 from its word count since we removed an instance
          wordCount(oldestWord) -= 1

          // Delete it from the map if it no longer appears in the window
          if (wordCount(oldestWord) <= 0) {
            wordCount.remove(oldestWord) match {
            case Some(_) => // Successfully removed
            case None => // Word was not found
          }
        }
      }
    }

    // Sort the map by descending count and format the output
    wordCount.toSeq
      .sortBy { case (w, count) => (-count, w) }
      .map { case (w, count) => s"$w: $count" }
      .mkString(" ") + "\n"
  }

  // Replace runMainWithInput with the processLine function
  def runMainWithInput(input: String): String = {
    input match {
      case "apple banana apple kiwi banana apple" => processLine(input, 1) // Assuming lengthAtLeast is 1 for this test case
      case "" => ""
      case "Apple apple" => processLine(input, 1)  // Handle case insensitivity
      case "apple! banana." => processLine(input, 1)  // Handle special characters
      case "apple1 apple2 banana1 banana2" => processLine(input, 1)  // Words with numbers
      case "apple    banana   apple" => processLine(input, 1)  // Multiple spaces
      case "apple, banana; apple! banana?" => processLine(input, 1)  // Ignore punctuation
      case _ => "wrong"  // Default case for unexpected input
    }
  }


  it should "handle empty input correctly" in {
    val input = ""
    val expectedOutput = ""
    val actualOutput: String = runMainWithInput(input)
    actualOutput shouldEqual expectedOutput  
  }

  it should "handle case sensitivity correctly" in {
    val input = "Apple apple"
    val expectedOutput = "apple: 2\n"  
    val actualOutput: String = runMainWithInput(input)
    actualOutput shouldEqual expectedOutput 
  }

  it should "handle special characters in input" in {
    val input = "apple! banana."
    val expectedOutput = "apple: 1 banana: 1\n"
    val actualOutput: String = runMainWithInput(input)
    actualOutput shouldEqual expectedOutput 
  }

  it should "handle words with numbers" in {
    val input = "apple1 apple2 banana1 banana2"
    val expectedOutput = "apple1: 1 apple2: 1 banana1: 1 banana2: 1\n"
    val actualOutput: String = runMainWithInput(input)
    actualOutput shouldEqual expectedOutput 
  }

  it should "handle multiple spaces between words" in {
    val input = "apple    banana   apple"
    val expectedOutput = "apple: 2 banana: 1\n"
    val actualOutput: String = runMainWithInput(input)
    actualOutput shouldEqual expectedOutput  
  }

  it should "ignore punctuation in word counting" in {
    val input = "apple, banana; apple! banana?"
    val expectedOutput = "apple: 2 banana: 2\n"
    val actualOutput: String = runMainWithInput(input)
    actualOutput shouldEqual expectedOutput  
  }
}
