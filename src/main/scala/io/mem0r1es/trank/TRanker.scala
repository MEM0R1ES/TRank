package io.mem0r1es.trank

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.mem0r1es.trank.pipeline.EntityLinking.linkEntities
import io.mem0r1es.trank.pipeline.NER.runNER
import io.mem0r1es.trank.pipeline.PreProcessor.preProcess
import io.mem0r1es.trank.pipeline.TypeRanking.rankTypes
import io.mem0r1es.trank.pipeline.TypeRetrieval.retrieveTypes
import io.mem0r1es.trank.ranking.ANCESTORS
import io.mem0r1es.trank.ranking.RankingAlgo
import java.io.InputStream
import java.io.ByteArrayInputStream

/**
 * TRanker class
 *
 * From a textual input rank its content according to the specified algorithm and the entities context.
 * Provide a Scala pipeline for:
 * <ul>
 *  <li>boilerplate removal on markup content</li>
 *  <li>Named Entity Recognition</li>
 *  <li>Entity linkage with DBpedia URIs</li>
 *  <li>Entity typing using a novel type hierarchy that combines DBpedia, Yago, and schema.org classes</li>
 *  <li>Type ranking based on algorithms that underwent thorough evaluation via crowdsourcing</li>
 * </ul>
 *
 * @param content textual stream to be processe
 * @param rankingAlgo specify ranking algorithm from package io.mem0r1es.trank.ranking
 * @param config specify another Typesafe config (i.e. indexes path)
 */
class TRanker(content: InputStream, rankingAlgo: RankingAlgo, config: Config) {

  config.checkValid(ConfigFactory.defaultReference(), "TRank")
  
  /**
   * Default to standard config.
   *
   * @param content textual stream to be processed
   * @param rankingAlgo specify ranking algorithm from package io.mem0r1es.trank.ranking
   */
  def this(content: InputStream, rankingAlgo: RankingAlgo) {
    this(content, rankingAlgo, ConfigFactory.load())
  }

  /**
   * Default to ANCESTORS ranking algorithm, and standard config.
   *
   * @param content textual stream to be processed
   */
  def this(content: InputStream) {
    this(content, new ANCESTORS, ConfigFactory.load())
  }

  /**
   * Default to standard config.
   *
   * @param contentStr text to be processed
   * @param rankingAlgo specify ranking algorithm from package io.mem0r1es.trank.ranking
   */
  def this(contentStr: String, rankingAlgo: RankingAlgo) {
    this(new ByteArrayInputStream(contentStr.getBytes()),
         rankingAlgo,
         ConfigFactory.load())
  }

  /**
   * Default to ANCESTORS ranking algorithm, and standard config.
   *
   * @param contentStr text to be processed
   */
  def this(contentStr: String) {
    this(new ByteArrayInputStream(contentStr.getBytes()),
         new ANCESTORS,
         ConfigFactory.load())
  }


  val contentRaw = content

  // TRank pipeline steps
  val contentPreProcessed = preProcess(content)
  private val entityLabels = runNER(contentPreProcessed)
  val entityToLabel = linkEntities(entityLabels, config)
  val entityURIs = entityToLabel.keySet
  val entityToTypes = retrieveTypes(entityURIs, config)
  val entityToTRankedTypes = rankTypes(entityToTypes, rankingAlgo, config)
}
