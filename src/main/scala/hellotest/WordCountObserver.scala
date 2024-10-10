package hellotest

// WordCountObserver defines the Observer interface for handling word count changes.
trait WordCountObserver {
  def updateWordCount(word: String, count: Int): Unit
}
