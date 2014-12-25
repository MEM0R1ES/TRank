package io.mem0r1es.trank.pipeline

import org.apache.tika.sax.BodyContentHandler
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.html.HtmlParser
import java.io.InputStream
import org.apache.tika.parser.ParseContext

/**
 * Pre-processor operations.
 */
object PreProcessor {

  /**
   * Runs the content pre-processing step (e.g., HTML tags removal).
   * Based on Apache Tika (http://tika.apache.org).
   *
   * @param content
   * @return cleaned text
   */
  def preProcess(content: InputStream): String = {
    extractTextFromHTML(content)
  }

  // extract text by removing markup tags, etc.
  private def extractTextFromHTML(content: InputStream): String = {
    val handler = new BodyContentHandler
    new HtmlParser().parse(content, handler, new Metadata, new ParseContext)

    handler.toString
  }
}
