package backend.engine

/**
  * A trait offering the compile method for texts of posts
  *
  * @author Stefan Bleibinhaus
  *
  */
trait TextCompiler {
  /**
    * Compiles the text of a post to an enriched abstract text and an enriched main text
    *
    * @param text The text of the post
    * @return Two strings. The first string is the enriched abstract text
    *  and the second string is the enriched main text.
    */
  def compile(text: String): (String, String)
}