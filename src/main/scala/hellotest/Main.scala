package hellotest

import scala.util.matching.Regex
import org.slf4j.{Logger, LoggerFactory}

import scala.io.StdIn

// Main object to wire up everything and run the program.
object Main {
  var logger: Logger = LoggerFactory.getLogger("Dynamic Word Cloud").nn

  def main(args: Array[String]): Unit = {
    // Validate and parse command-line arguments
    val config = ArgsParser.parseArgs(args).get
    logger.info(s"Parsed command-line arguments: $config")

    val delimiterPattern: Regex = "[^\\p{Alpha}0-9']+".r

    // Create word counter and observer
    val wordCounter = new WordCounter(config.windowSize, config.cloudSize, config.batchSize)

    // Create and start the Les Misérables processor
    // val lesMisProcessor = new LesMisProcessor(delimiterPattern, config.lengthAtLeast, wordCounter, logger)

    // Define the inputSource to read from standard input
    val inputSource: () => Option[String] = () => {
      val input = StdIn.readLine() // Read a line from standard input
      Option(input) // Wrap the input in Option
    }

    // Create and start the input processor
    val inputProcessor: InputProcessor = new InputProcessor(delimiterPattern, config.lengthAtLeast, wordCounter, inputSource, logger)

    // Add shutdown hook
    sys.addShutdownHook {
      logger.info("Shutting down gracefully...")
    }

    //lesMisProcessor.processFile("/workspace/project-1b-dynamic-word-cloud-team-5/src/main/scala/hellotest/lesmis.txt")

    // Process input
    inputProcessor.processInput()
  }
}
