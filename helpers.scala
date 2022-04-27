sealed trait Extraction

case object NotFound extends Extraction
case class Found(extracted: String, rest: String) extends Extraction

/** Extractors allow you to check if input starts with specified symbol and then
  * extract matched symbol and rest of input. For example:
  *
  * `key("abc = \"something\"") == Found("abc", " = \"something\"")`
  * `quoted("abc = \"something\"") == NotFound`
  * `symbol('=')("= \"something\"") == Found("=", " \"something\""")`
  */
object Extractors {
  private val Whitespace = """^\s+""".r
  private val Key = """^[a-z]+""".r

  def whitespace(input: String): Extraction =
    Whitespace.findFirstMatchIn(input) match {
      case None    => NotFound
      case Some(m) => Found(input.take(m.end), input.drop(m.end))
    }

  def key(input: String): Extraction =
    Key.findFirstMatchIn(input) match {
      case None    => NotFound
      case Some(m) => Found(input.take(m.end), input.drop(m.end))
    }

  def quoted(input: String): Extraction =
    if (input.headOption == Some('"'))
      input.indexWhere(_ == '"', 1) match {
        case -1 => NotFound
        case n  => Found(input.take(n + 1), input.drop(n + 1))
      }
    else NotFound

  def symbol(expected: Char)(input: String): Extraction =
    if (input.headOption == Some(expected))
      Found(expected.toString(), input.drop(1))
    else
      NotFound
}
