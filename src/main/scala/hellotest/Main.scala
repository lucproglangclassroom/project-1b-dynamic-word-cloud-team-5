package hellotest

import scala.util.matching.Regex
import org.slf4j.{Logger, LoggerFactory}
import scala.io.StdIn

object Main {
  val logger: Logger = LoggerFactory.getLogger("Dynamic Word Cloud").nn
  
  def main(args: Array[String]): Unit = {
    // Validate and parse command-line arguments
    val config = ArgsParser.parseArgs(args).get
    logger.info(s"Parsed command-line arguments: $config")

    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r

    // Create word counter
    val wordCounter = new WordCounter(config.windowSize, config.cloudSize, config.batchSize)

    // Define the input source to read from standard input
    val inputSource: () => Option[String] = () => Option(StdIn.readLine())

    // Create and start the input processor
    val inputProcessor = new InputProcessor(delimiterPattern, config.lengthAtLeast, wordCounter, inputSource, logger)

    // Add shutdown hook
    sys.addShutdownHook {
      logger.info("Shutting down gracefully...")
    }

    // Process input
    inputProcessor.processInput()
  }
}
