package hellotest

import scopt.OParser

// Default values
case class Config(cloudSize: Int = 10, lengthAtLeast: Int = 6, windowSize: Int = 1000, batchSize: Int = 100)

object ArgsParser {
  val builder = OParser.builder[Config]
  val parser = {
    import builder._
    OParser.sequence(
      programName("topwords"),
      head("topwords", "1.0"),

      opt[Int]('c', "cloud-size")  //arg 1 cloud size
        .valueName("<cloudSize>")
        .action((x, c) => c.copy(cloudSize = x))
        .text("Size of the word cloud (default: 10)"),

      opt[Int]('l', "length-at-least") //arg 2 minimum length
        .valueName("<lengthAtLeast>")
        .action((x, c) => c.copy(lengthAtLeast = x))
        .text("Minimum length of a word to be considered (default: 6)"),

      opt[Int]('w', "window-size") //arg 3 window size
        .valueName("<windowSize>")
        .action((x, c) => c.copy(windowSize = x))
        .text("Size of the moving window (default: 1000)"),
      
      opt[Int]('b', "batch-size") //arg 4 batch size
        .valueName("<batchSize>")
        .action((x, c) => c.copy(batchSize = x))
        .text("Number of words to process before printing (default: 100)")
    )
  }

  def parseArgs(args: Array[String]): Option[Config] = {
    OParser.parse(parser, args, Config())
  }
}