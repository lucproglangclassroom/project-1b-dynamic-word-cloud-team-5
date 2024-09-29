package hellotest

import scala.util.matching.Regex
import org.log4s.getLogger

// Main object to wire up everything and run the program.
object Main {
  private val logger = getLogger("Main")

  def main(args: Array[String]): Unit = {
    // Validate and parse command-line arguments
    val config = ArgsParser.parseArgs(args).get
    logger.info(s"Parsed command-line arguments: $config")

    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r

    // Create word counter and observer
    val wordCounter = new WordCounter(config.windowSize, config.cloudSize)

    // Create and start the input processor
    val inputProcessor = new InputProcessor(delimiterPattern, config.lengthAtLeast, wordCounter)

    // Add shutdown hook
    sys.addShutdownHook {
      logger.info("Shutting down gracefully...")
    }

    // Process input
    inputProcessor.processInput()
  }
}
