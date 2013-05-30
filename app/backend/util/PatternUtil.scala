package backend.util

/**
  * An object containing methods used in pattern matching
  *
  * @author Stefan Bleibinhaus
  *
  */
object PatternUtil {
  /**
    * Tries to find a char in a pattern
    *
    * @param c
    * @param pattern
    * @return true if the char could be found, false otherwise
    */
  def matches(c: Char, pattern: String): Boolean = pattern.r.findFirstIn(c.toString).isDefined
}