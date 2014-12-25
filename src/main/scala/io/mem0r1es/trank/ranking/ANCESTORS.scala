package io.mem0r1es.trank.ranking

import java.net.URI

/**
 * ANCESTORS ranking algorithm.
 */
class ANCESTORS extends RankingAlgo {

  /**
   * Rank types by inverse-sort on the # of ANCESTORS contained in the type set.
   *
   * @see RankingAlgo
   */
  override def rank(entityTypes: Map[URI, HierInfo]): Seq[(URI, Double)] = {

    def score(path: Seq[URI]): Double = {
      path.filter(entityTypes.contains(_)).length
    }

    entityTypes.map {
      case (k, v) => (k, score(v.path))
    }.toSeq.sortBy(_._2).reverse
  }
}
