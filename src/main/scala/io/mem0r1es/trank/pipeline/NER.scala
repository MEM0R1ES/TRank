package io.mem0r1es.trank.pipeline

import edu.stanford.nlp.ie.crf.CRFClassifier
import java.util.Properties

/**
 * Named entity recognition operations.
 */
object NER {

  private val props = new Properties
  props.put("annotators", "tokenize")
  private val classifier = CRFClassifier.getClassifierNoExceptions(
    "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz")
  // TODO : can be cached?

  /**
   * Runs the named entity recognizer on the given content.
   * Based on Stanford Named Entity Recognizer (http://nlp.stanford.edu/software/CRF-NER.shtml).
   *
   * @param content
   * @return set: named entity labels
   */
  def runNER(content: String): Set[String] = {
    val annotatedContent = classifier.classifyWithInlineXML(content)

    extractEntities(annotatedContent)
  }

  // extract entities based on PERSON, LOCATION and ORGANIZATION tag
  private def extractEntities(content: String): Set[String] = {
    extractSingleType(content, "<PERSON>", "</PERSON>") ++
            extractSingleType(content, "<LOCATION>", "</LOCATION>") ++
            extractSingleType(content, "<ORGANIZATION>", "</ORGANIZATION>")
  }

  // extract specified tag-enclosed data from given content
  private def extractSingleType(content: String, openTag: String, closeTag: String): Set[String] = {
    val fragments = content.split(openTag)

    fragments.drop(1).map { fragment =>
      fragment.split(closeTag)(0)
    }.toSet
  }
}
