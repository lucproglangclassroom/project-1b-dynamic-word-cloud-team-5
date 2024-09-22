import scala.io.StdIn
import scala.util.matching.Regex
import org.apache.commons.collections4.queue.CircularFifoQueue

object Main {

  private val DefaultQueueSize = 10

  def main(args: Array[String]): Unit = {
    // Validate and parse command-line arguments
    val queueSize = parseArguments(args)

    // Initialize the CircularFifoQueue with the specified size
    val queue = new CircularFifoQueue[String](queueSize)

    // Regular expression to match non-alphanumeric characters
    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r

    // Process input lines
    Iterator.continually(Option(StdIn.readLine()))
      .takeWhile(_.isDefined)
      .foreach { lineOpt =>
        lineOpt.foreach { line =>
          // Split the input line into tokens and add them to the queue
          val tokens = delimiterPattern.split(line).filter(_.nonEmpty)
          tokens.foreach { token =>
            queue.add(token)
            println(queue)
          }
        }
      }
  }

  /** Parses command-line arguments and returns the queue size.
    * @param args the command-line arguments
    * @return the queue size
    */
  private def parseArguments(args: Array[String]): Int = {
    if (args.length > 1) {
      System.err.nn.println("Usage: ./target/universal/stage/bin/consoleapp [last_n_words]")
      System.exit(2)
    }

    try {
      args.headOption.map(_.toInt).filter(_ > 0).getOrElse(DefaultQueueSize)
    } catch {
      case _: NumberFormatException =>
        System.err.nn.println("Argument should be a natural number.")
        System.exit(4)
        DefaultQueueSize // This line will never be reached due to System.exit
    }
  }
}