import scopt.OParser

// Default values
case class Config(cloudSize: Int = 10, lengthAtLeast: Int = 6, windowSize: Int = 1000)

object ArgsParser {
  val builder = OParser.builder[Config]
  val parser = {
    import builder._
    OParser.sequence(
      programName("topwords"),
      head("topwords", "1.0"),
      opt[Int]('c', "cloud-size")
        .valueName("<cloudSize>")
        .action((x, c) => c.copy(cloudSize = x))
        .text("Size of the word cloud (default: 10)"),
      opt[Int]('l', "length-at-least")
        .valueName("<lengthAtLeast>")
        .action((x, c) => c.copy(lengthAtLeast = x))
        .text("Minimum length of a word to be considered (default: 6)"),
      opt[Int]('w', "window-size")
        .valueName("<windowSize>")
        .action((x, c) => c.copy(windowSize = x))
        .text("Size of the moving window (default: 1000)")
    )
  }

  def parseArgs(args: Array[String]): Option[Config] = {
    OParser.parse(parser, args, Config())
  }
}


