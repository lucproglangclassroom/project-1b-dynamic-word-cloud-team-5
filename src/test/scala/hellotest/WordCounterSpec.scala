package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class WordCounterTest extends AnyFlatSpec with Matchers with MockitoSugar {
  "WordCounter" should "notify observers correctly" in {
    val windowSize = 5
    val cloudSize = 3
    val batchSize = 2
    val wordCounter = new WordCounter(windowSize, cloudSize, batchSize)

    // Create and register a mock observer
    val observer = mock[WordCountObserver]
    wordCounter.addObserver(observer)

    // Process enough words to trigger notifications
    wordCounter.processWord("a")
    wordCounter.processWord("b")
    wordCounter.processWord("c")
    wordCounter.processWord("aa")
    wordCounter.processWord("bb")
    wordCounter.processWord("aa")

    // Verify that observers are notified exactly once for each unique word count
    verify(observer, times(1)).nn.updateWordCount("aa", 1)
    verify(observer, times(1)).nn.updateWordCount("bb", 1)
  }
}
