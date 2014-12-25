package io.mem0r1es.trank.pipeline

import java.net.URI

import scala.util.parsing.json.JSON

import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

import com.typesafe.config.Config

import io.mem0r1es.trank.ranking.HierInfo
import io.mem0r1es.trank.ranking.RankingAlgo
import io.mem0r1es.trank.util.IndexUtils
import io.mem0r1es.trank.util.TRankIndexType

/**
 * Type ranking operations.
 */
object TypeRanking {

  /**
   * Retrieves hierarchic information for all URI of RDF type and rank them.
   * Based on Apache Lucene (http://lucene.apache.org) via specific indexes.
   *
   * These indexes can be download here: http://exascale.info/sites/default/files/uploaded/trank/trank-indexes.tgz
   * The .tgz extracted folder should be placed at project root (i.e. "TRank/trank-indexes").
   * Indexes path can be changed in Typesafe config ("reference.conf").
   *
   * @param entityTypes map: entity URI -> set: URI types
   * @param rankingAlgo ranking algorithm from package io.mem0r1es.trank.ranking
   * @param config alternative Typesafe config
   * @return map: entity URI -> set: ranked pairs URI type/score
   */
  def rankTypes(entityTypes: Map[URI, Set[URI]],
                rankingAlgo: RankingAlgo,
                config: Config): Map[URI, Seq[(URI, Double)]] = {
    entityTypes.map{
      case (uri, types) =>
        uri -> rankingAlgo.rank(types.map{ t =>
          t -> queryHier(t, config)
        }.toMap)
    }
  }

  // queries hierarchic information for a given URI
  private def queryHier(typeURI: URI, config: Config): HierInfo = {
    val searcher = IndexUtils.getIndexSearcher(TRankIndexType.PATH_INDEX, config) // get indexes (default: /trank-indexes)
    val query = new TermQuery(new Term("uri", typeURI.toString))
    val docs = searcher.search(query, 1)

    if (docs.scoreDocs.nonEmpty) {
      val d = searcher.doc(docs.scoreDocs(0).doc)
      val level = d.get("level").toInt
      val path = JSON.parseFull(d.get("path"))

      path.get match {
        case l: List[_] => new HierInfo(level, l.map(t => new URI(t.toString)))
        case _ => new HierInfo(level, Seq.empty)
      }
    } else {
      new HierInfo(-1, Seq.empty)
    }
  }
}
