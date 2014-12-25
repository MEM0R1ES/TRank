package io.mem0r1es.trank.ranking

import java.net.URI

/**
 * TRanker algorithms require:
 * <ul>
 *     <li>rank method based on hierarchic information</li>
 * </ul>
 */
trait RankingAlgo {

  /**
   * Ranks URI of RDF type with hierarchic information according to the highest score.
   *
   * @param entityTypes map: URI type -> hierarchic information
   * @return Seq: pairs URI type/score
   */
  def rank(entityTypes: Map[URI, HierInfo]): Seq[(URI, Double)]
}