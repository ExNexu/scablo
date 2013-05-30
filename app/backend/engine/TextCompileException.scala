package backend.engine

/**
  * An exception class for exceptions during text compilation
  *
  * @author Stefan Bleibinhaus
  *
  */
class TextCompileException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)