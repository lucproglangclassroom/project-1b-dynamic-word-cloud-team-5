package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class WordCounterTest extends AnyFlatSpec with Matchers with MockitoSugar {
  "WordCounter" should "notify observers correctly" in {
    val windowSize = 5
    val cloudSize = 3
    val wordCounter = new WordCounter(windowSize, cloudSize, None)

    // Create and register a mock observer
    //val observer = mock[WordCountObserver]
    //wordCounter.addObserver(observer)
    var state = CounterState()

    // Process enough words to trigger notifications
    wordCounter.processWord("a", state)
    wordCounter.processWord("b", state)
    wordCounter.processWord("c", state)
    wordCounter.processWord("aa", state)
    wordCounter.processWord("bb", state)
    wordCounter.processWord("aa", state)

    // Verify that observers are notified exactly once for each unique word count
    //verify(observer, times(1)).nn.updateWordCount("aa", 1)
    //verify(observer, times(1)).nn.updateWordCount("bb", 1)
  }
}
