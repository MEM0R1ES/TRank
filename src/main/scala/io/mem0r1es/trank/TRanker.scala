package io.mem0r1es.trank

import java.net.URI

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
 * <ol>
 *    <li>boilerplate removal on markup content</li>
 *    <li>Named Entity Recognition</li>
 *    <li>Entity linkage with DBpedia URIs</li>
 *    <li>Entity typing using a novel type hierarchy that combines DBpedia, Yago, and schema.org classes</li>
 *    <li>Type ranking based on algorithms that underwent thorough evaluation via crowdsourcing</li>
 * </ol>
 *
 * @param content
 * @param rankingAlgo ranking algorithm from package io.mem0r1es.trank.ranking
 * @param config alternative Typesafe config (i.e. indexes path)
 */
class TRanker(content: InputStream, rankingAlgo: RankingAlgo, config: Config) {

  config.checkValid(ConfigFactory.defaultReference(), "TRank")
  
  /**
   * Default to standard config.
   *
   * @param content
   * @param rankingAlgo ranking algorithm from package io.mem0r1es.trank.ranking
   */
  def this(content: InputStream, rankingAlgo: RankingAlgo) {
    this(content, rankingAlgo, ConfigFactory.load())
  }

  /**
   * Default to ANCESTORS ranking algorithm, and standard config.
   *
   * @param content
   */
  def this(content: InputStream) {
    this(content, new ANCESTORS, ConfigFactory.load())
  }

  /**
   * Default to standard config.
   *
   * @param contentStr
   * @param rankingAlgo ranking algorithm from package io.mem0r1es.trank.ranking
   */
  def this(contentStr: String, rankingAlgo: RankingAlgo) {
    this(new ByteArrayInputStream(contentStr.getBytes()),
         rankingAlgo,
         ConfigFactory.load())
  }

  /**
   * Default to ANCESTORS ranking algorithm, and standard config.
   *
   * @param contentStr
   */
  def this(contentStr: String) {
    this(new ByteArrayInputStream(contentStr.getBytes()),
         new ANCESTORS,
         ConfigFactory.load())
  }


  val contentRaw: InputStream = content

  // TRank pipeline steps
  /** 1. Pre-proccessor */
  val contentPreProcessed: String = preProcess(content)

  /** 2. Named entity recognition */
  private val entityLabels: Set[String] = runNER(contentPreProcessed)

  /** 3. Entity linking (URI) */
  val entityToLabel: Map[URI, String] = linkEntities(entityLabels, config)
  val entityURIs: Set[URI] = entityToLabel.keySet

  /** 4. Type retrieval (URI) */
  val entityToTypes: Map[URI, Set[URI]] = retrieveTypes(entityURIs, config)

  /** 5. Type ranking */
  val entityToTRankedTypes: Map[URI, Seq[(URI, Double)]] = rankTypes(entityToTypes, rankingAlgo, config)
}
